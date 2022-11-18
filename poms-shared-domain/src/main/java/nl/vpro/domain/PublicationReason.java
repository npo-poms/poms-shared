package nl.vpro.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Indicates a reason for publication. This part of the a {@code MediaChange} object.
 * In ElasticSearch is it written directly into the {@code _doc} representing the {@MediaObject} itself though.
 *
 * @author Michiel Meeuwissen
 * @since 7.1
 */
@Getter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publicationReason", namespace = Xmlns.API_NAMESPACE)
@Slf4j
public class PublicationReason implements Serializable {

    //@Serial
    private static final long serialVersionUID = -5898117026516765909L;

    /**
     * Multiple reasons can be joined with this, to encode the in one String.
     * <p>
     * Uses now ASCII Record seperator RS
     */
    public static final String RECORD_SPLITTER = "\u241E";

    /**
     * A reason can be joined with its publication time.
     * <p>
     * Uses now ASCII Unit seperator US
     */
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

    public PublicationReason(@NonNull String value, @Nullable Instant publicationDate) {
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

    public static String toRecords(Collection<PublicationReason> reasons) {
        return reasons.stream().map(PublicationReason::toRecord).collect(Collectors.joining(RECORD_SPLITTER));
    }

    public static PublicationReason parseOne(String string) {
        final String[] reasonAndDate = string.split(FIELD_SPLITTER, 2);
        final Instant instant;
        if (reasonAndDate.length > 1 && reasonAndDate[1].length() > 0) {
            instant = Instant.ofEpochMilli(Long.parseLong(reasonAndDate[1]));
        } else {
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
}
