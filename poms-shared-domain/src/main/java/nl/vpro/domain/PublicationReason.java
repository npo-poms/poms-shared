package nl.vpro.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static java.util.Comparator.*;

/**
 * Indicates a reason for publication. This part of the a {@code MediaChange} object.
 * In ElasticSearch is it written directly into the {@code _doc} representing the {@code MediaObject} itself though.
 * <p>
 * So a reason in the 'repubReason' field in the database looks like
 * {@code <some string>[\t<some other string>][..]]}
 * So, multiple reasons are joined with {@link #REASON_SPLITTER}
 * <p>
 * Resulting in two reason in the api, where the strings are joined with a string representing the mechanism and a timestamp. (The two fields in {@link PublicationReason}).
 *  Encoded  {@code republication|<some string>␟1686051496927}
 * and {@code republication|<some other string>␟1686051496927} (constructed with {@link #toRecord(Instant)}).
 * <p>
 *  While transferring multiple such reasons to the publisher multiple of such string representations are joined with {@link #RECORD_SPLITTER}.
 * </p>
 *
 *
 *
 * @author Michiel Meeuwissen
 * @since 7.1
 */
@Getter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publicationReasonType", namespace = Xmlns.API_NAMESPACE)
@Slf4j
public class PublicationReason implements Serializable, Comparable<PublicationReason> {

    @Serial
    private static final long serialVersionUID = -5898117026516765909L;

    /**
     * Multiple reasons can be joined with this, to encode them in one String.
     * <p>
     * Uses  ASCII Record separator RS
     */
    public static final String RECORD_SPLITTER = "␞";

    /**
     * A reason can be joined with its publication time. Not in the database, just in the string representation of a
     * set of reasons (as put on headers).
     * <p>
     * Uses  ASCII Unit separator US
     */
    public static final String FIELD_SPLITTER = "␟";

    /**
     * Multiple reasons can be collected in the database, in that case they are joined with this.
     */
    public static final String REASON_SPLITTER = "\t";

    /**
     * An explicit reason can be stored in the database, but they get prefixed by the mechanism that caused them
     */
    public static final String REASON_PREFIX_SPLITTER = "|";

    /**
     * The pattern for a valid reason in the database.
     */
    public static final String REASON_PATTERN = "[^" + RECORD_SPLITTER + FIELD_SPLITTER  + "]*";


    /**
     * The string representing the reason for publication. This may just be some mechanism, like the ones found in  nl.vpro.domain.media.PublicationUpdate.Reasons}, but this may also be a prefix for some explicit reason set in the database (seperated by {@link #REASON_PREFIX_SPLITTER}).
     */
    @XmlValue
    String value;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    protected PublicationReason() {

    }

    public PublicationReason(
        @NonNull String value,
        @Nullable Instant publicationDate) {
        this.value = value;
        this.publishDate = publicationDate;
    }

    public PublicationReason parent() {
        return new PublicationReason(parentReason(value), getPublishDate());
    }

    public String toRecord(Instant now) {
        return value + FIELD_SPLITTER + (publishDate == null ? now.toEpochMilli() : publishDate.toEpochMilli());
    }

    @Override
    public String toString() {
        return value + ":" + publishDate;
    }


    public static String parentReason(String reason) {
        return "parent: " + reason;
    }

    public static String toRecords(Instant now, List<PublicationReason> reasons, long maxLength, String mid, boolean mergeDuplicates) {
        final StringBuilder builder = new StringBuilder();
        PublicationReason prevReason = null;
        for (PublicationReason reason : reasons) {

            boolean needsSplitter = !builder.isEmpty();
            if (prevReason != null) {
                if (DateUtils.isAfter(prevReason.publishDate, reason.publishDate)) {
                    log.warn("The list seems to be ordered wrong");
                }
                if (mergeDuplicates && Objects.equals(prevReason.getValue(), reason.getValue())) {
                    builder.delete(builder.length() - prevReason.toRecord(now).length(), builder.length());
                    needsSplitter = false;

                }
            }
            if (needsSplitter){
                builder.append(RECORD_SPLITTER);
            }
            String s = reason.toRecord(now);
            builder.append(s);
            prevReason = reason;
            if (builder.length() >= maxLength) {
                log.error("Reason header for {} is ridiculously long ({} reasons, {} chars). Will be truncated", mid, reasons.size(), builder.length());
                break;
            }
        }
        return builder.toString();
    }

    public static PublicationReason parseOne(final String string, String logContext) {
        final String[] reasonAndDate = string.split(FIELD_SPLITTER, 2);
        final Instant instant;
        if (reasonAndDate.length > 1 && !reasonAndDate[1].isEmpty()) {
            instant = Instant.ofEpochMilli(Long.parseLong(reasonAndDate[1]));
        } else {
            log.warn("{} No time found in '{}'. Creating a publication reason without publish date", logContext, string);
            instant = null;
        }
        return new PublicationReason(reasonAndDate[0], instant);
    }

    /**
     * @param string The string to parse
     * @param logContext Something which will be added to the warnings in the log if something is odd. This may give some context to the person encountering the issue.
     */
    public static PublicationReason[] parseList(
        final String string,
        final String logContext) {
        List<PublicationReason> result = new ArrayList<>();
        if (string != null && !string.isEmpty()) {
            for (var s : string.split(RECORD_SPLITTER)) {
                try {
                    result.add(parseOne(s, logContext + "(" + string + " )"));
                } catch(Exception e) {
                    log.warn("{} Couldn't parse {}: {} {}", logContext, s, e.getClass().getName(), e.getMessage());
                }
            }
        } else {
            log.warn("{} No reasons found in {}", logContext, string);
        }
        return result.toArray(PublicationReason[]::new);
    }

    private static final Comparator<PublicationReason> COMPARATOR =
        comparing(
            PublicationReason::getPublishDate, nullsLast(naturalOrder()))
            .thenComparing(
                PublicationReason::getValue, nullsLast(naturalOrder())
            );

    @Override
    public int compareTo(@NonNull PublicationReason o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationReason that = (PublicationReason) o;

        if (!Objects.equals(value, that.value)) return false;
        return Objects.equals(publishDate, that.publishDate);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        return result;
    }

    public static abstract class Reasons {
        public static final String DIRECT              = "direct";
        public static final String NOTIFY_ONLINE       = "notify online";
        public static final String NOTIFY_OFFLINE      = "notify offline";

        /**
         * Republished because received a restriction xml for the object.
         */
        public static final String RESTRICTION         = "restriction";

        /**
         * Republished because embargo (activation/deactivation) on one (or more) of the restrictions
         */
        public static final String RESTRICTIONS        = "restrictions";

        public static final String NOTIFY              = "notify";

        public static final String SORT_DATE           = "sortdate";
        public static final String PUBLICATION         = "publication";
        public static final String REPUBLICATION       = "republication";
        public static final String EMBARGO_PUBLISH     = "embargo publication";
        public static final String EMBARGO_REVOKE      = "embargo revoke";
        public static final String DELETION            = "deletion";
        public static final String NO_SCHEDULE_EVENTS = "no scheduleevents left";
        public static final String SCHEDULE_EVENT      = "scheduleevent";

        public static final String IMAGE_EMBARGO       = "image embargo";
        public static final String PARENT_REVOKED      = "parent revoked";
        public static final String PARENT_PUBLISHED    = "parent published";

        public static final String AVAILABLE_SUBTITLES = "available subtitles";

        public static final String MERGED              = "merged %s>%s";
        public static final String PARENT_MERGED       = "parent merged >%s";

        public static final String EXPLICIT            = "explicit fill";

        public static final String UNDELETED_PROGRAM_IMPORTER = "Undeleted by program importer";

        public static final String SEGMENT = "segment change";

        public static final String LOCATION_REVOKE = "location revoke";
        public static final String LOCATION_PUBLISH = "location publish";

        public static final String RCRS                 = "rcrs";

        public static final String TYPE_CHANGED = "type of parent changed";
        public static final String NUMBER_CHANGED = "number changed";

        public static final String REALIZED_PREDICTION = "realized prediction";
        public static final String REVOKED_PREDICTION = "revoked prediction";


        /**
         * type:mid:reason
         */
        public static final String REPUBLISHING_DESCENDANTS_TEMPLATE = "republishing descendants of %s:%s:%s";


        public static final String NEP_EXISTS = "nep notify for non existing object";
    }
}
