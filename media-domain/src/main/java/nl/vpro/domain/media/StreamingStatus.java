package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Displayable;
import nl.vpro.i18n.Dutch;

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

    @Getter @Setter
    @Column(name="streamingplatformstatus_withdrm_offline")
    Instant withDrmOffline = null;

    @Getter @Setter
    @Column(name="streamingplatformstatus_withoutdrm_offline")

    Instant withoutDrmOffline = null;


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


    public StreamingStatus(
        Value withDrm,
        Value withoutDrm
    ) {
        this.withDrm = withDrm;
        this.withoutDrm = withoutDrm;
    }

    @lombok.Builder
    private StreamingStatus(
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

    public StreamingStatus copy() {
        return new StreamingStatus(withDrm, withoutDrm);
    }


    @NonNull
    public static StreamingStatus copy(StreamingStatus of) {
        return of == null ? unset() : of.copy();
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


    public boolean hasDrm() {
        return withDrm == Value.ONLINE;
    }

    public boolean onDvrWithDrm() {
        return hasDrm() && withDrmOffline != null && withDrmOffline.isAfter(Instant.now());
    }

    public boolean hasWithoutDrm() {
        return withoutDrm == Value.ONLINE;
    }

    public boolean isAvailable() {
        return (hasDrm() && online(withDrmOffline)) || (hasWithoutDrm() && online(withoutDrmOffline));
    }

    private boolean online(Instant offline) {
        return offline == null || offline.isAfter(Instant.now());
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

    /**
     * Matches with an encryption.
     */
    public boolean matches(Encryption encryption) {
        return
            (encryption == null && (hasDrm() || hasWithoutDrm())) ||
            (encryption == Encryption.DRM && hasDrm()) ||
            (encryption == Encryption.NONE && hasWithoutDrm());
    }

    /**
     *
     */
    public boolean matches(Prediction prediction) {
        return prediction != null && (
            hasDrm() || // MSE-3992
                matches(prediction.getEncryption())
        );
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
            String postFix = "";
            if (onDvrWithDrm()) {

                builder.append(connector)
                    .append("in DVR window");
                builder.append(" tot ")
                    .append(Dutch.formatSmartly(withDrmOffline));
                connector = " en ";
            } else if (hasDrm()) {
                builder.append(connector).append("met");
                connector = " en ";
                postFix = " DRM";
            }
            if (hasWithoutDrm()) {
                builder.append(connector).append("zonder");
                postFix = " DRM";
            }
            builder.append(postFix);
        } else {
            builder.append("Niet beschikbaar");
        }
        return builder.toString();


    }

    @Override
    public String toString() {
        return withDrm + (withDrmOffline != null ? ("(-" + withDrmOffline + ")") : "") +  "_" +
            withoutDrm + (withoutDrmOffline != null ? ("(-" + withoutDrmOffline + ")") : "");
    }

    /**
     * See <a href="http://wiki.publiekeomroep.nl/display/poms/Locations+and+predictions#Locationsandpredictions-Locations,streamingplatformandpredictions">wiki</a>
     *
     * Given a prediction shows what kind of locations must be created by the authority location service.
     *
     * @return A list of {@link Encryption}s.
     */
    public List<Encryption> getEncryptionsForPrediction(Prediction prediction) {

        if (prediction == null || ! prediction.isPlannedAvailability()) {
            return Arrays.asList();
        }
        Encryption e = prediction.getEncryption();
        if (e == null) {
            e = Encryption.NONE;
        }

        switch (withDrm) {
            case OFFLINE:
            case UNSET: {
                switch (withoutDrm) {
                    case OFFLINE:
                    case UNSET:
                        return Arrays.asList();
                    case ONLINE:
                        switch (e) {
                            case DRM:
                                return Arrays.asList();
                            case NONE:
                                return Arrays.asList(
                                    Encryption.NONE);

                        }
                }
            }
            case ONLINE: {
                switch (withoutDrm) {
                    case OFFLINE:
                    case UNSET:
                        return Arrays.asList(Encryption.DRM);
                    case ONLINE:
                        switch (e) {
                            case DRM:
                                return Arrays.asList(Encryption.DRM);
                            case NONE:
                                return Arrays.asList(Encryption.DRM, Encryption.NONE);

                        }
                }
            }

        }
        throw new IllegalStateException();

    }

    public Instant getOffline(boolean drm) {
        if (drm) {
            return getWithDrmOffline();
        } else {
            return getWithoutDrmOffline();
        }
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
