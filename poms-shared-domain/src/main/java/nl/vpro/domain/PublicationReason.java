package nl.vpro.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
 *
 * @author Michiel Meeuwissen
 * @since 7.1
 */
@Getter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publicationReason", namespace = Xmlns.API_NAMESPACE)
@Slf4j
public class PublicationReason implements Serializable, Comparable<PublicationReason> {

    //@Serial
    private static final long serialVersionUID = -5898117026516765909L;

    /**
     * Multiple reasons can be joined with this, to encode the in one String.
     * <p>
     * Uses  ASCII Record separator RS
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final String RECORD_SPLITTER = "\u241E";

    /**
     * A reason can be joined with its publication time.
     * <p>
     * Uses  ASCII Unit separator US
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final String FIELD_SPLITTER = "\u241F";


    public static final String REASON_PATTERN = "[^" + RECORD_SPLITTER + FIELD_SPLITTER  + "]*";



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

    public String toRecord() {
        return value + FIELD_SPLITTER + (publishDate == null ? "" : publishDate.toEpochMilli());
    }

    @Override
    public String toString() {
        return value + ":" + publishDate;
    }


    public static String parentReason(String reason) {
        return "parent: " + reason;
    }

    public static String toRecords(List<PublicationReason> reasons, long maxLength, String mid, boolean mergeDuplicates) {
        StringBuilder builder = new StringBuilder();
        PublicationReason prevReason = null;
        for (PublicationReason reason : reasons) {


            boolean needsSplitter = builder.length() > 0;
            if (prevReason != null) {
                if (DateUtils.isAfter(prevReason.publishDate, reason.publishDate)) {
                    log.warn("The list seems to be ordered wrong");
                }
                if (mergeDuplicates && Objects.equals(prevReason.getValue(), reason.getValue())) {
                    builder.delete(builder.length() - prevReason.toRecord().length(), builder.length());
                    needsSplitter = false;

                }
            }
            if (needsSplitter){
                builder.append(RECORD_SPLITTER);
            }
            String s = reason.toRecord();
            builder.append(s);
            prevReason = reason;
            if (builder.length() >= maxLength) {
                log.error("Reason header for {} is ridiculously long ({} reasons, {} chars). Will be truncated", mid, reasons.size(), builder.length());
                break;
            }
        }
        return builder.toString();
    }

    public static PublicationReason parseOne(final String string) {
        final String[] reasonAndDate = string.split(FIELD_SPLITTER, 2);
        final Instant instant;
        if (reasonAndDate.length > 1 && reasonAndDate[1].length() > 0) {
            instant = Instant.ofEpochMilli(Long.parseLong(reasonAndDate[1]));
        } else {
            log.warn("No time found in '{}'. Creating a publication reason without publish date", string);
            instant = null;
        }
        return new PublicationReason(reasonAndDate[0], instant);
    }

    public static PublicationReason[] parseList(String string) {
        List<PublicationReason> result = new ArrayList<>();
        if (string != null && !string.isEmpty()) {
            for (var s : string.split(RECORD_SPLITTER)) {
                try {
                    result.add(parseOne(s));
                } catch(Exception e) {
                    log.warn("Couldn't parse {}: {} {}", s, e.getClass().getName(), e.getMessage());
                }
            }
        } else {
            log.warn("No reasons found in {}", string);
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
}
