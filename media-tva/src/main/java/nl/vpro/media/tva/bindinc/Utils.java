package nl.vpro.media.tva.bindinc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.*;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.Channel;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Comparator.*;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
public final class Utils {

    private Utils() {
        // utility class
    }

    public static final String HEADER_CHANNEL   = "bindinc-channel";
    public static final String HEADER_DAY       = "bindinc-day";
    public static final String HEADER_TIMESTAMP = "bindinc-timestamp";

    public static final String BINDINC_CRID_PREFIX = "crid://media-press.tv/";
    public static final String BINDINC_PERSON_PREFIX = "crid://bindinc/person/";
    public static final String BINDINC_MID_PREFIX = "BINDINC_";

    public static final String BINDINC_GENRE_PREFIX = "urn:bindinc:genre:";

    public static final Set<String> BINDINC_GENRE_IGNORE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        BINDINC_GENRE_PREFIX + "Overige",
        BINDINC_GENRE_PREFIX + "Radio"
    )));



    private static final Pattern FILE_NAME = Pattern.compile("(?:.*/)?(.*)day(.*?)([0-9]{8})\\.(?:.*\\.)?xml");
    private static final DateTimeFormatter LOCAL_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    //https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8031085
    private static final DateTimeFormatter TIMESTAMP = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

    public static  Optional<BindincFile> parseFileName(String fileName) {
        BindincFile.Builder builder = BindincFile.builder();
        if (fileName == null){
            return Optional.empty();
        }
        Matcher matcher = FILE_NAME.matcher(fileName);
        if (matcher.matches()) {
            try {
                builder.timestamp(LocalDateTime.parse(matcher.group(1), TIMESTAMP));
            } catch (Exception e) {
                log.warn("for {}: {}", fileName, e.getMessage());
            }
            Channel channel;
            try {
                channel = Channel.findByBindincId(matcher.group(2));
                builder.channel(channel);
            } catch (Exception e) {
                log.error("for {}: {}", fileName, e.getMessage());
                channel = null;
            }
            LocalDate day;
            try {
                day = LocalDate.parse(matcher.group(3), LOCAL_DATE);
                builder.day(day);
            } catch (Exception e) {
                log.error("for {}: {}", fileName, e.getMessage());
                day = null;
            }
            return Optional.of(builder.build());
        } else {
            return Optional.empty();
        }
    }


    /**
     * For the items which dont have a proper mid (bindinc uses tva:BasicDescription/tva:OtherIdentifier/tva:OtherIdentifier[@type='broadcaster:npo:productid'] to store,
     * we just generate one based on the bindinc id.
     *
     * Note that this id seems to correspond to _schedule events_ rather then actual programs.  In practice this probably means
     * that only on PO-channels we'll have programs with multiple schedule events.
     */
    public static void assignBindincMidIfNecessary(Program p) {
        if (StringUtils.isEmpty(p.getMid())) {
            for (String c : p.getCrids()) {
                if (c.startsWith(BINDINC_CRID_PREFIX)) {
                    p.setMid(BINDINC_MID_PREFIX + p.getCrids().get(0).substring(BINDINC_CRID_PREFIX.length()));
                }
            }
        }
    }

    static final Genre MOVIE = new Genre(MediaClassificationService.getInstance().getTerm("3.0.1.2"));

    public static void recognizeMovie(Program p) {
        if (p.getType() != ProgramType.MOVIE) {
            if (p.getGenres().contains(MOVIE)) {
                p.setType(ProgramType.MOVIE);
            }
        }
    }

    public static  void parseFileName(Exchange exchange) {
        // default correlation
        exchange.getIn().setHeader(Exchange.CORRELATION_ID, exchange.getIn().getMessageId());
        String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        if (fileName == null) {
            return;
        }
        Utils.parseFileName(fileName).ifPresent(bf -> {
            exchange.getIn().setHeader(HEADER_TIMESTAMP, bf.getTimestamp());
            exchange.getIn().setHeader(HEADER_CHANNEL, bf.getChannel());
            exchange.getIn().setHeader(HEADER_DAY, bf.getDay());
            exchange.getIn().setHeader(Exchange.CORRELATION_ID, bf.getCorrelation());
        });
    }

    public static void ready(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if (body instanceof Temp) {
            ((Temp) body).deleteFile();
        }

    }


    /**
     * Meta data for a Bindinc file.
     */
    @Getter
    public static class BindincFile implements Comparable<BindincFile> {

        static final Comparator<BindincFile> COMPARATOR =
            nullsLast(
                comparing(BindincFile::getDay, nullsLast(naturalOrder()))).thenComparing(BindincFile::getChannel, nullsLast(naturalOrder())).thenComparing(BindincFile::getTimestamp, nullsLast(naturalOrder()));
        private final LocalDateTime timestamp;
        private final Channel channel;
        private final LocalDate day;
        private final String correlation;

        @lombok.Builder(builderClassName = "Builder")
        public BindincFile(LocalDateTime timestamp, Channel channel, LocalDate day, String correlation) {
            this.timestamp = timestamp;
            this.channel = channel;
            this.day = day;
            this.correlation = correlation == null ? (channel == null ? "" : channel.name()) + "/" + day : correlation;
        }


        @Override
        public int compareTo(BindincFile o) {
            return COMPARATOR.compare(this, o);
        }
    }


    public static class Converters implements TypeConverters {
        final Map<String, List<Temp>> TEMPS = new HashMap<>();


        /**
         * Converts input stream to a {@link Temp} object. Stores the inputstream as a temporary file.
         *
         * All these objects will be administrated on correlation id. If one of them is used, (because is is converted to an inputstream {@link #convertToInputStream(Temp, Exchange)},
         * then all temp files for some correlation id will be deleted.
         */
        @Converter
        public Temp convertToTemp(InputStream inputStream, Exchange exchange) throws IOException {
            return correlation(
                new Temp(inputStream, exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)),
                exchange
            );
        }

        @Converter
        public Temp convertToTemp(GenericFile<File> file, Exchange exchange) throws IOException {
            File f = file.getFile();
            if (f.exists()) {
                return correlation(
                    new Temp(f, exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)),
                    exchange);
            } else {
                log.info("{} doesn't exist", f);
                return null;
            }
        }
        private Temp correlation(Temp temp, Exchange exchange) {
            String correlation = exchange.getIn().getHeader(Exchange.CORRELATION_ID, String.class);
            TEMPS.computeIfAbsent(correlation, (c) -> new ArrayList<>()).add(temp);
            return temp;
        }
        @Converter
        public InputStream convertToInputStream(Temp temp, Exchange exchange) throws FileNotFoundException {
            if (temp.file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(temp.file);
                List<Temp> temps = TEMPS.remove(exchange.getIn().getHeader(Exchange.CORRELATION_ID, String.class));
                if (temps != null) {
                    temps.forEach(t -> {
                        if (t.file != temp.file) {
                            t.deleteFile();
                        }
                    });
                }
                return fileInputStream;
            } else {
                log.warn("File deleted already");
                return new ByteArrayInputStream("File deleted already".getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Used for aggregation of GenericFiles. See {@link Converters#convertToTemp(InputStream, Exchange)}
     */

    public static class Temp  {
        final File file;
        private  Temp(@NonNull File source, @NonNull String filename) throws IOException {
            file = createTempFile(filename);
            Files.copy(source.toPath(), file.toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
        }

        private  Temp(@NonNull InputStream in, @NonNull String filename) throws IOException {
            file = createTempFile(filename);
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(in, out);
            }
        }

        void deleteFile() {
            if (file.delete()) {
                log.debug("Deleted {}", file);
            }
        }

        private static File createTempFile(@NonNull  String filename) throws IOException {
            File file;
            if (filename != null) {
                final String[] split = filename.split("/");
                file = File.createTempFile("tmp", "-" + split[split.length - 1]);
            } else {
                file = File.createTempFile("tmp", "-bindinc");
            }
            file.deleteOnExit();
            log.debug("Create temp file {}", file);
            return file;
        }
        @Override
        public String toString() {
            return file.toString();
        }
    }
}

