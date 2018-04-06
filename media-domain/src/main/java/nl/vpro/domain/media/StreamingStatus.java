package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Embeddable
@XmlRootElement(name="streamingStatus")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamingStatus implements Serializable, Displayable  {

    @XmlType(name = "streamingStatusValue")
    public enum Value {
        OFFLINE,
        ONLINE,
        UNSET
    }

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="streamingplatformstatus_withdrm")
    @XmlAttribute
    Value withDrm = Value.UNSET;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="streamingplatformstatus_withoutdrm")
    @XmlAttribute
    Value withoutDrm = Value.UNSET;

    public StreamingStatus() {
    }

    public static StreamingStatus unset() {
        return new StreamingStatus(Value.UNSET, Value.UNSET);
    }
     public static StreamingStatus withDrm(StreamingStatus existing) {
        return new StreamingStatus(Value.ONLINE, existing.withoutDrm);
    }
    public static StreamingStatus withoutDrm(StreamingStatus existing) {
        return new StreamingStatus(existing.withDrm, Value.ONLINE);
    }
    public static StreamingStatus withAndWithoutDrm() {
        return new StreamingStatus(Value.ONLINE, Value.ONLINE);
    }
    public static StreamingStatus offline() {
        return new StreamingStatus(Value.OFFLINE, Value.OFFLINE);
    }
     public static StreamingStatus offlineDrm(StreamingStatus existing) {
        return new StreamingStatus(Value.OFFLINE, existing.withoutDrm);
    }
     public static StreamingStatus offlineWithoutDrm(StreamingStatus existing) {
        return new StreamingStatus(existing.withDrm, Value.OFFLINE);
    }

    public static List<StreamingStatus> availableStatuses() {
        return Arrays.asList(withDrm(offline()), withDrm(unset()), withoutDrm(offline()), withoutDrm(unset()), withAndWithoutDrm());
    }

      public static List<StreamingStatus> notAvailableStatuses() {
          return Arrays.asList(unset(), offline());
    }


    @lombok.Builder
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
        return withDrm == Value.ONLINE;
    }


    public boolean hasWithoutDrm() {
        return withoutDrm == Value.ONLINE;
    }

    public boolean isAvailable() {
        return hasDrm() || hasWithoutDrm();
    }

    public static Encryption preferredEncryption(StreamingStatus streamingStatus) {
        if (streamingStatus == null || streamingStatus.hasWithoutDrm()) {
            return Encryption.NONE;
        } else if (streamingStatus.hasDrm()) {
            return Encryption.DRM;
        } else {
            return Encryption.NONE;
        }
    }

    public boolean matches(Encryption encryption) {
        return (encryption == null && (hasDrm() || hasWithoutDrm())) ||
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
    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        if (isAvailable()) {
            String connector = " ";
            builder.append("Beschikbaar");
            if (hasDrm()) {
                builder.append(connector).append("met");
                connector = " en ";
            }
             if (hasWithoutDrm()) {
                builder.append(connector).append("zonder");
                connector = " en ";
            }
            builder.append(" DRM");
        } else {
            builder.append("Niet beschikbaar");
        }
        return builder.toString();


    }

    @Override
    public String toString() {
        return withDrm + "_"+ withoutDrm;
    }

    protected void calcCRC32(CRC32 result) {
         if (getWithDrm() != null) {
             result.update(getWithDrm().ordinal());
         } else {
             result.update(0);

         }
        if (getWithoutDrm() != null) {
            result.update(getWithoutDrm().ordinal());
        } else {
            result.update(0);
        }
    }
}
