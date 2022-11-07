package nl.vpro.domain;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlTransient
@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public abstract class Change<T>  {

    @XmlAttribute
    @XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    @XmlAttribute
    private String id;

    @XmlAttribute
    @Setter(AccessLevel.PUBLIC)
    private Boolean deleted;


    @XmlAttribute
    private Boolean tail = null;

    /**
     * A 'skipped' change object is for some reason not relevant, and will not be marshalled in
     * result streams.
     */
    @XmlTransient
    @Setter(AccessLevel.PUBLIC)
    private boolean skipped;

    //@XmlElement(name = "object")
    private T object;

    protected Change() {
    }


    protected Change(String id, T object, Boolean deleted) {
        this.id = id;
        this.object = object;
        this.deleted = deleted == null || ! deleted ? null : true;
    }

    public boolean isDeleted() {
        return deleted != null ? deleted : false;
    }

    public boolean isTail() {
        return tail != null ? tail : false;
    }

    public boolean isNotSkipped() {
        return ! isSkipped();
    }

    @Override
    public String toString() {
        return (isTail() ? "TAIL:" : "" ) + publishDate + ":" + id +
                (isTail() ? "" : (":" + object + (isDeleted() ? ":DELETED" : "")));

    }

    @Getter
    @XmlAccessorType(XmlAccessType.NONE)
    @XmlType(name = "changeReason")
    public static class Reason {

        /**
         * Multiple reasons can be joined with this, to encode the in one String.
         * <p>
         * Uses now ASCII Record seperator RS
         */
        public static final String RECORD_SPLITTER         = "\u241E";

        /**
         * A reason can be joined with its publication time.
         * <p>
         *  Uses now ASCII Unit seperator US
         */
        public static final String FIELD_SPLITTER          = "\u241F";



        @XmlValue
        String reason;

        @XmlAttribute
        @XmlJavaTypeAdapter(InstantXmlAdapter.class)
        @XmlSchemaType(name = "dateTime")
        @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
        @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
        private Instant publishDate;

        protected Reason() {

        }
        public Reason(String reason, Instant publicationDate) {
            this.reason = reason;
            this.publishDate = publicationDate;
        }

        public static Reason parseOne(String string) {
            final String[] reasonAndDate = string.split(FIELD_SPLITTER, 2);
            final Instant instant;
            if (reasonAndDate.length >  1) {
                instant = Instant.ofEpochMilli(Long.parseLong(reasonAndDate[1]));
            } else {
                instant = null;
            }
            return new Reason(reasonAndDate[0], instant);
        }
        public static Reason[] parseList(String string) {
            List<Reason> result = new ArrayList<>();
            if (string != null && !string.isEmpty()) {
                for (var s : string.split(RECORD_SPLITTER)) {
                    result.add(parseOne(s));
                }
            }
            return result.toArray(Reason[]::new);
        }
    }
}
