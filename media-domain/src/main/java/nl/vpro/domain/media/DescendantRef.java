package nl.vpro.domain.media;

import java.io.Serial;
import java.io.Serializable;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author roekoe
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descendantRefType")
@JsonPropertyOrder({
    "midRef",
    "urnRef",
    "type"
})
public class DescendantRef implements Comparable<DescendantRef>, Serializable {

    @Serial
    private static final long serialVersionUID = 2305207507673462765L;

    @XmlAttribute
    protected String urnRef;

    @XmlAttribute
    protected String midRef;

    @XmlAttribute
    protected MediaType type;

    public DescendantRef() {
    }

    @lombok.Builder
    public DescendantRef(String midRef, String urnRef, MediaType type) {
        this.urnRef = urnRef;
        this.midRef = midRef;
        this.type = type;
    }


    /**
     * @since 5.9
     */
    public static DescendantRef of(MemberRef r) {
        return new DescendantRef(r.midRef, r.urnRef, r.getType());
    }


    public static DescendantRef forOwner(MediaObject media) {
        return new DescendantRef(media.getMid(), media.getUrn(), MediaType.getMediaType(media));
    }

    public String getUrnRef() {
        return urnRef;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull DescendantRef descendantRef) {
        return (urnRef != null && descendantRef.urnRef != null) ? urnRef.compareTo(descendantRef.urnRef)
            : (midRef != null && descendantRef.midRef != null) ? midRef.compareTo(descendantRef.midRef)
            : (urnRef != null) ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        DescendantRef that = (DescendantRef)o;

        if(midRef == null && that.midRef != null) {
            return false;
        }
        if(urnRef == null && that.urnRef != null) {
            return false;
        }
        if(midRef != null && !midRef.equals(that.midRef)) {
            return false;
        }
        return urnRef == null || urnRef.equals(that.urnRef);
    }

    public String getMidRef() {
        return midRef;
    }

    public void setMidRef(String midRef) {
        this.midRef = midRef;
    }

    @Override
    public int hashCode() {
        if(midRef != null) {
            return midRef.hashCode();
        } else if(urnRef != null) {
            return urnRef.hashCode();
        }

        return super.hashCode();
    }

    @Override
    public String toString() {
        return "DescendantRef{" +
            "type=" + type +
            ", midRef='" + midRef + '\'' +
            '}';
    }
}
