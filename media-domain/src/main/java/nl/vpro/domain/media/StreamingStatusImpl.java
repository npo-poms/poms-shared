package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.time.Instant;
import java.util.zip.CRC32;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

/**
 * The streaming stati are currently stored as fields of the {@link MediaObject}.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Embeddable
@XmlType(name = "streamingStatus")
@XmlRootElement(name="streamingStatus")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamingStatusImpl implements StreamingStatus {

    @Serial
    private static final long serialVersionUID = -6853692159691281271L;

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

    /**
     * @deprecated This was never used
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Deprecated
    @Transient
    Instant withDrmOffline = null;


    /**
     * @deprecated This was never used
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Deprecated
    @Transient
    Instant withoutDrmOffline = null;


    /**
     * Audio is currently always without DRM.
     * @since 7.7
     */
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="streamingplatformstatus_audio_withoutdrm")
    @XmlAttribute
    Value audioWithoutDrm = Value.UNSET;

    public StreamingStatusImpl() {
    }


    StreamingStatusImpl(
        Value withDrm,
        Value withoutDrm,
        Value audioWithoutDrm
    ) {
        this.withDrm = withDrm;
        this.withoutDrm = withoutDrm;
        this.audioWithoutDrm = audioWithoutDrm;
    }

    @lombok.Builder(builderClassName = "Builder")
    private StreamingStatusImpl(
        Value withDrm,
        Instant withDrmOffline,
        Value withoutDrm,
        Instant withoutDrmOffline,
        Value audioWithoutDrm
    ) {
        this.withDrm = withDrm;
        this.withDrmOffline = withDrmOffline;
        this.withoutDrm = withoutDrm;
        this.withoutDrmOffline = withoutDrmOffline;
        this.audioWithoutDrm = audioWithoutDrm;
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
        if (!(o instanceof StreamingStatus that)) return false;

        if (withDrm != that.getWithDrm()) return false;
        if (withoutDrm != that.getWithoutDrm()) return false;
        return audioWithoutDrm == that.getAudioWithoutDrm();
    }


    @Override
    public String toString() {
        return withDrm + (withDrmOffline != null ? ("(-" + withDrmOffline + ")") : "") +  "_" +
            withoutDrm + (withoutDrmOffline != null ? ("(-" + withoutDrmOffline + ")") : "") +
            (audioWithoutDrm == Value.ONLINE ? "_A:" + audioWithoutDrm : "");
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
        if (audioWithoutDrm != null && audioWithoutDrm != Value.UNSET) {
            result.update(this.getAudioWithoutDrm().ordinal());
        }
    }

    // empty, helping javadoc plugin @ java 11
    public static class Builder {

    }
}
