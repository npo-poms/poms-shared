package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.i18n.Dutch;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface StreamingStatus extends Serializable, Displayable {

    @XmlType(name = "streamingStatusValue")
    enum Value {
        OFFLINE,
        ONLINE,
        UNSET
    }


    static StreamingStatusImpl.Builder builder() {
        return StreamingStatusImpl.builder();
    }

    static StreamingStatusImpl unset() {
        return new StreamingStatusImpl(Value.UNSET, Value.UNSET);
    }

    static StreamingStatusImpl withDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(Value.ONLINE, existing.getWithoutDrm());
    }

    static StreamingStatusImpl withoutDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(existing.getWithDrm(), Value.ONLINE);
    }

    static StreamingStatusImpl withAndWithoutDrm() {
        return new StreamingStatusImpl(Value.ONLINE, Value.ONLINE);
    }

    static StreamingStatusImpl offline() {
        return new StreamingStatusImpl(Value.OFFLINE, Value.OFFLINE);
    }

    static StreamingStatusImpl offlineDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(Value.OFFLINE, existing.getWithoutDrm());
    }

    static StreamingStatusImpl offlineWithoutDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(existing.getWithDrm(), Value.OFFLINE);
    }

    static List<StreamingStatusImpl> availableStatuses() {
        return Arrays.asList(withDrm(offline()), withDrm(unset()), withoutDrm(offline()), withoutDrm(unset()), withAndWithoutDrm());
    }

    static List<StreamingStatusImpl> notAvailableStatuses() {
        return Arrays.asList(unset(), offline());
    }


    default ReadonlyStreamingStatus copy() {
        return new ReadonlyStreamingStatus(getWithDrm(), getWithDrmOffline(),  getWithoutDrm(), getWithoutDrmOffline());
    }


    Value getWithDrm();

    Value getWithoutDrm();

    Instant getWithDrmOffline();

    Instant getWithoutDrmOffline();

    @NonNull
    static StreamingStatus copy(StreamingStatus of) {
        return of == null ? unset() : of.copy();
    }


    default boolean hasDrm() {
        return getWithDrm() == Value.ONLINE;
    }

    default boolean onDvrWithDrm() {
        Instant withDrmOffline = getWithDrmOffline();
        return hasDrm() && withDrmOffline != null && withDrmOffline.isAfter(Changeables.clock().instant());
    }

    default boolean hasWithoutDrm() {
        return getWithoutDrm() == Value.ONLINE;
    }

    default boolean isAvailable() {
        return (hasDrm() && online(getWithDrmOffline())) || (hasWithoutDrm() && online(getWithoutDrmOffline()));
    }

    static boolean online(Instant offline) {
        return offline == null || offline.isAfter(Changeables.clock().instant());
    }

    static Encryption preferredEncryption(StreamingStatus streamingStatus) {
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
    default boolean matches(Encryption encryption) {
        return
            (encryption == null && (hasDrm() || hasWithoutDrm())) ||
                (encryption == Encryption.DRM && hasDrm()) ||
                (encryption == Encryption.NONE && hasWithoutDrm());
    }

    /**
     *
     */
    default boolean matches(Prediction prediction) {
        return prediction != null && (
            hasDrm() || // MSE-3992
                matches(prediction.getEncryption())
        );
    }


    @Override
    default String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        if (isAvailable()) {
            String connector = " ";
            builder.append("Beschikbaar");
            String postFix = "";
            if (onDvrWithDrm()) {

                builder.append(connector)
                    .append("in DVR window");
                builder.append(" tot ")
                    .append(Dutch.formatSmartly(getWithDrmOffline()));
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

    /**
     * See <a href="http://wiki.publiekeomroep.nl/display/poms/Locations+and+predictions#Locationsandpredictions-Locations,streamingplatformandpredictions">wiki</a>
     *
     * Given a prediction shows what kind of locations must be created by the authority location service.
     *
     * @return A list of {@link Encryption}s.
     */
    default List<Encryption> getEncryptionsForPrediction(Prediction prediction) {

        if (prediction == null || !prediction.isPlannedAvailability()) {
            return Arrays.asList();
        }
        Encryption e = prediction.getEncryption();
        if (e == null) {
            e = Encryption.NONE;
        }

        switch (getWithDrm()) {
            case OFFLINE:
            case UNSET: {
                switch (getWithoutDrm()) {
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
                switch (getWithoutDrm()) {
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

    default Instant getOffline(boolean drm) {
        if (drm) {
            return getWithDrmOffline();
        } else {
            return getWithoutDrmOffline();
        }
    }

}
