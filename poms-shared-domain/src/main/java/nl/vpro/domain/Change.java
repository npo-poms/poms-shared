package nl.vpro.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlTransient
@Getter
@Setter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Change<T>  {


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private Boolean deleted;

    @XmlAttribute
    private Boolean tail = null;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Change)) return false;

        Change<?> that = (Change<?>) o;

        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (deleted != null ? !deleted.equals(that.deleted) : that.deleted != null) return false;
        if (tail != null ? !tail.equals(that.tail) : that.tail != null) return false;
        return object != null ? object.equals(that.object) : that.object == null;
    }

    @Override
    public int hashCode() {
        int result = publishDate != null ? publishDate.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (deleted != null ? deleted.hashCode() : 0);
        result = 31 * result + (tail != null ? tail.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return (isTail() ? "TAIL:" : "" ) + publishDate + ":" + id +
                (isTail() ? "" : (":" + object + (isDeleted() ? ":DELETED" : "")));

    }

}
