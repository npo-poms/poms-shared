package nl.vpro.media.tva.bindinc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.*;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class Utils {

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
            if (channel != null) {
                builder.correlation(channel.name() + day);
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
    public static void bindincMids(MediaTable table) {
        for (Program p : table.getProgramTable()) {
            if (StringUtils.isEmpty(p.getMid())) {
                for (String c : p.getCrids()) {
                    if (c.startsWith(BINDINC_CRID_PREFIX)) {
                        p.setMid(BINDINC_MID_PREFIX + p.getCrids().get(0).substring(BINDINC_CRID_PREFIX.length()));
                    }
                }
            }
        }
    }

  @Getter
    public static class BindincFile implements Comparable<BindincFile> {

        static final Comparator<BindincFile> COMPARATOR =  Comparator.nullsLast(Comparator.comparing(BindincFile::getDay).thenComparing(BindincFile::getChannel).thenComparing(BindincFile::getTimestamp));
        private final LocalDateTime timestamp;
        private final Channel channel;
        private final LocalDate day;
        private final String correlation;

        @lombok.Builder(builderClassName = "Builder")
        public BindincFile(LocalDateTime timestamp, Channel channel, LocalDate day, String correlation) {
            this.timestamp = timestamp;
            this.channel = channel;
            this.day = day;
            this.correlation = correlation;
        }


        @Override
        public int compareTo(BindincFile o) {
            return COMPARATOR.compare(this, o);
        }
    }
}

