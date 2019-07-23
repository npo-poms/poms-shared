package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.zip.CRC32;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Embeddable
@XmlRootElement(name="streamingStatus")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamingStatusImpl implements StreamingStatus {

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="streamingplatformstatus_withdrm")
    @XmlAttribute
    Value withDrm = Value.UNSET;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="streamingplatformstatus_withoutdrm")
    @XmlAttribute
    Value withoutDrm = Value.UNSET;

    @Getter
    @Setter
    @Column(name="streamingplatformstatus_withdrm_offline")
    Instant withDrmOffline = null;

    @Getter
    @Setter
    @Column(name="streamingplatformstatus_withoutdrm_offline")
    Instant withoutDrmOffline = null;


    public StreamingStatusImpl() {
    }


    public StreamingStatusImpl(
        Value withDrm,
        Value withoutDrm
    ) {
        this.withDrm = withDrm;
        this.withoutDrm = withoutDrm;
    }

    @lombok.Builder(builderClassName = "Builder")
    private StreamingStatusImpl(
        Value withDrm,
        Instant withDrmOffline,
        Value withoutDrm,
        Instant withoutDrmOffline
    ) {
        this.withDrm = withDrm;
        this.withDrmOffline = withDrmOffline;
        this.withoutDrm = withoutDrm;
        this.withoutDrmOffline = withoutDrmOffline;
    }

    public void set(boolean drm, Value value) {
        if (drm) {
            setWithDrm(value);
        } else {
            setWithoutDrm(value);
        }
    }

    public void set(boolean drm, Instant offline) {
        if (drm) {
            setWithDrmOffline(offline);
        } else {
            setWithoutDrmOffline(offline);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamingStatusImpl that = (StreamingStatusImpl) o;

        if (withDrm != that.withDrm) return false;
        return withoutDrm == that.withoutDrm;
    }


    @Override
    public String toString() {
        return withDrm + (withDrmOffline != null ? ("(-" + withDrmOffline + ")") : "") +  "_" +
            withoutDrm + (withoutDrmOffline != null ? ("(-" + withoutDrmOffline + ")") : "");
    }

    protected void calcCRC32(CRC32 result) {
         if (getWithDrm() != null) {
             result.update(getWithDrm().ordinal());
             if (withDrmOffline != null) {
                 result.update(withDrmOffline.hashCode());
             }
         } else {
             result.update(0);

         }
        if (getWithoutDrm() != null) {
            result.update(getWithoutDrm().ordinal());
            if (withoutDrmOffline != null) {
                result.update(withoutDrmOffline.hashCode());
            }
        } else {
            result.update(0);
        }
    }
}
