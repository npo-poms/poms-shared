package nl.vpro.domain;

import lombok.Getter;

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

@Getter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publicationReason")
public class PublicationReason implements Serializable {

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


    @XmlValue
    String reason;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    protected PublicationReason() {

    }

    public PublicationReason(@NonNull String reason, @Nullable Instant publicationDate) {
        this.reason = reason;
        this.publishDate = publicationDate;
    }

    public PublicationReason parent() {
        return new PublicationReason("parent: " + getReason(), getPublishDate());
    }

    public String toRecord() {
        return reason + FIELD_SPLITTER + (publishDate == null ? "" : publishDate.toEpochMilli());
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
                result.add(parseOne(s));
            }
        }
        return result.toArray(PublicationReason[]::new);
    }
}
