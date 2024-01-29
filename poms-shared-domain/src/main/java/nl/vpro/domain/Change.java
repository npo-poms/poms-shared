package nl.vpro.domain;

import lombok.*;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;

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

    /**
     * @since 7.9
     */
    public static boolean isTail(@Nullable Change<?> t) {
        return t != null && t.isTail();
    }

    public boolean isNotSkipped() {
        return ! isSkipped();
    }

    @Override
    public String toString() {
        return (isTail() ? "TAIL:" : "" ) + publishDate + "," + id +
                (isTail() ? "" : (":" + object + (isDeleted() ? ":DELETED" : "")));

    }

}
