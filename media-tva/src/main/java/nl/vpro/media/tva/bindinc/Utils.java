package nl.vpro.media.tva.bindinc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.*;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.*;
import nl.vpro.media.tva.Constants;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Comparator.*;

/**
 * Utilities related to syncing TVA files from Bindinc.
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

    /**
     * Can be used as an argument to {@link nl.vpro.media.tva.saxon.extension.EpgGenreFunction#setIgnore(Set)}
     */
    public static final Set<String> BINDINC_GENRE_IGNORE = Set.of(
        BINDINC_GENRE_PREFIX + "Overige",
        BINDINC_GENRE_PREFIX + "Radio",
        BINDINC_GENRE_PREFIX + "Magazine"
    );


    private static final Pattern FILE_NAME = Pattern.compile("(?:.*/)?(.*)day(.*?)([0-9]{8})\\.(?:.*\\.)?xml");
    private static final DateTimeFormatter LOCAL_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    //https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8031085
    private static final DateTimeFormatter TIMESTAMP = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

    public static  Optional<BindincFile> parseFileName(String fileName) {
        final BindincFile.Builder builder = BindincFile.builder();
        if (fileName == null){
            return Optional.empty();
        }
        final Matcher matcher = FILE_NAME.matcher(fileName);
        if (matcher.matches()) {
            try {
                String timestamp = matcher.group(1);
                builder.timestamp(LocalDateTime.parse(timestamp, TIMESTAMP));
            } catch (Exception e) {
                // just a timestamp of the incoming file, not very important
                log.warn("for {}: {} ({})", fileName, e.getMessage(), TIMESTAMP);
            }
            Channel channel;
            try {
                String fromName = matcher.group(2);
                String mapped = Constants.getBindincChannelMappings().get(fromName);
                if (mapped == null) {
                    log.warn("No bindinc channel found for {}", fromName);
                    channel = null;
                } else {
                    channel = Channel.valueOf(mapped);
                }
            } catch (Exception e) {
                log.error("for {}: {}", fileName, e.getMessage());
                channel = null;
            }
            builder.channel(channel);
            LocalDate day;
            try {
                day = LocalDate.parse(matcher.group(3), LOCAL_DATE);
            } catch (Exception e) {
                log.error("for {}: {}", fileName, e.getMessage());
                day = null;
            }
            builder.day(day);
            return Optional.of(builder.build());
        } else {
            return Optional.empty();
        }
    }


    /**
     * For the items which don't have a proper mid (bindinc uses tva:BasicDescription/tva:OtherIdentifier/tva:OtherIdentifier[@type='broadcaster:npo:productid'] to store,
     * we just generate one based on the bindinc id.
     * <p>
     * Note that this id seems to correspond to _schedule events_ rather than actual programs.  In practice this probably means
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
        List<Temp> list = (List<Temp>) exchange.getProperty(ExchangePropertyKey.GROUPED_EXCHANGE);
        for (Temp t : list) {
            t.deleteFile();
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


    /**
     *
     */
    public static class GenericFileAggregationStrategy extends GroupedBodyAggregationStrategy {

        public GenericFileAggregationStrategy(CamelContext context)  {
            super();
            context.getTypeConverterRegistry().addTypeConverters(new Converters());
        }
        @Override
        @SuppressWarnings("unchecked")
        public void onCompletion(Exchange exchange) {
            if (exchange != null && isStoreAsBodyOnCompletion()) {
                List<Object> list = (List<Object>) exchange.getProperty(ExchangePropertyKey.GROUPED_EXCHANGE);
                if (list != null) {
                    exchange.getIn().setBody(list.get(list.size() - 1));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Converters implements TypeConverters {
        /**
         * Converts input stream to a {@link Temp} object. Stores the {@link InputStream} as a temporary file.
         * <p>
         * All these objects will be administrated on correlation id. If one of them is used (because it is converted to an {@link InputStream} {@link #convertToInputStream(Temp, Exchange)}), then all temp files for some correlation id will be deleted.
         */
        @Converter
        public Temp convertToTemp(InputStream inputStream, Exchange exchange) throws IOException {
            final String correlation = exchange.getIn().getHeader(Exchange.CORRELATION_ID, String.class);
            return new Temp(inputStream, exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), correlation);
        }

        /**
         *
         */
        @Converter
        public Temp convertToTemp(byte[] bytes, Exchange exchange) throws IOException {
            return convertToTemp(new ByteArrayInputStream(bytes), exchange);
        }

        /**
         *
         */
        @Converter
        public Temp convertToTemp(List<Temp> list, Exchange exchange) throws IOException {
            exchange.getIn().setHeader("TEMPS", list);
            return list.get(list.size() - 1);
        }

        @Converter
        public Temp convertToTemp(GenericFile<File> file, Exchange exchange) throws IOException {
            File f = file.getFile();
            if (f.exists()) {
                final String correlation = exchange.getIn().getHeader(Exchange.CORRELATION_ID, String.class);
                return new Temp(f, exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), correlation);
            } else {
                log.info("{} doesn't exist", f);
                return null;
            }
        }


        @Converter
        public InputStream convertToInputStream(
            Temp temp, Exchange exchange) throws FileNotFoundException {
            if (temp.file.exists()) {
                return new FileInputStream(temp.file);
            } else {
                log.warn("File of {}  deleted already , so it can't be converted to an inputStream {}", temp, Arrays.asList(temp.deletedBy), new Exception());
                throw new  IllegalStateException("File deleted already");
            }
        }
    }

    /**
     * Used for aggregation of GenericFiles. See {@link Converters#convertToTemp(InputStream, Exchange)}
     *
     * If you naively aggregate genericfiles, then the files will be deleted before they are used.
     *
     * <pre>
     * {@code
     *         getContext().getTypeConverterRegistry().addTypeConverters(new Utils.Converters());
     *         ...
     *         .process(Utils::parseFileName) // sets correlation id
     *         .convertBodyTo(Temp.class)     // body is a 'Temp' object, containing the temp file and some meta data
     *         .aggregate(header(Exchange.CORRELATION_ID), groupedBody()) // body will become a list of 'Temp' objects
     *         .completionInterval(aggregationInterval.toMillis())
     *         .convertBodyTo(Temp.class) // will take the last 'Temp' object from the list
     *         ...
     *         .process(Utils::ready) // deletes all temp files associated with the current aggregation
     * }
     * </pre>
     *
     *
     */
    @Getter
    public static class Temp  {
        final File file;
        final Instant created = Instant.now();
        final String correlation;
        StackTraceElement[] deletedBy;
        private  Temp(@NonNull File source, @NonNull String filename, @NonNull String correlation) throws IOException {
            this.file = createTempFile(filename);
            this.correlation = correlation;
            Files.copy(source.toPath(), file.toPath(), REPLACE_EXISTING, COPY_ATTRIBUTES);
        }

        private  Temp(@NonNull InputStream in, @NonNull String filename, @NonNull  String correlation) throws IOException {
            this.file = createTempFile(filename);
            this.correlation = correlation;
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(in, out);
            }
        }

        void deleteFile() {
            if (file.exists()) {
                if (file.delete()) {
                    log.debug("Deleted {}", file);
                    deletedBy = Thread.currentThread().getStackTrace();
                } else {
                    log.warn("File {} could not be deleted", file);
                }
            } else {
                log.debug("File {} does not exist", file);
            }
        }

        private static File createTempFile(@NonNull  String filename) throws IOException {
            final File file;
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
            return file.toString() + " created " + created +  " (" + correlation + ")";
        }

        /**
         * @since 7.6.2
         */
        public boolean isEmpty() {
            return file.length() == 0;
        }
    }
}

