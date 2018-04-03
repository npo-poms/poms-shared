package nl.vpro.domain.media.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.Encryption;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Embeddable
@XmlRootElement(name="streamingStatus")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamingStatus implements Serializable{


    public enum Value {
        OFFLINE,
        ONLINE,
        UNSET
    }

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column
    @XmlAttribute
    Value withDrm;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column
    @XmlAttribute
    Value withoutDrm;

    public StreamingStatus() {
    }

    public static StreamingStatus unset() {
        return new StreamingStatus(Value.UNSET, Value.UNSET);
    }
    public static StreamingStatus withDrm() {
        return new StreamingStatus(Value.ONLINE, Value.OFFLINE);
    }
    public static StreamingStatus withoutDrm() {
        return new StreamingStatus(Value.OFFLINE, Value.ONLINE);
    }
    public static StreamingStatus withAndWithoutDrm() {
        return new StreamingStatus(Value.ONLINE, Value.ONLINE);
    }
    public static StreamingStatus offline() {
        return new StreamingStatus(Value.OFFLINE, Value.OFFLINE);
    }

     public static List<StreamingStatus> availableStatuses() {
        return Arrays.asList(withDrm(), withoutDrm(), withAndWithoutDrm());
    }


    public StreamingStatus(Value withDrm, Value withoutDrm) {
        this.withDrm = withDrm;
        this.withoutDrm = withoutDrm;
    }


    public void set(boolean drm, Value value) {
        if (drm) {
            setWithDrm(value);
        } else {
            setWithoutDrm(value);
        }
    }


    public boolean hasDrm() {
        return withoutDrm == Value.ONLINE;
    }


    public boolean hasWithoutDrm() {
        return withoutDrm == Value.ONLINE;
    }

    public boolean isAvailable() {
        return hasDrm() || hasWithoutDrm();
    }

    public boolean matches(Encryption encryption) {
        return encryption == null ||
            (encryption == Encryption.DRM && hasDrm()) ||
            (encryption == Encryption.NONE && hasWithoutDrm());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamingStatus that = (StreamingStatus) o;

        if (withDrm != that.withDrm) return false;
        return withoutDrm == that.withoutDrm;
    }

    @Override
    public int hashCode() {
        int result = withDrm != null ? withDrm.hashCode() : 0;
        result = 31 * result + (withoutDrm != null ? withoutDrm.hashCode() : 0);
        return result;
    }
}
