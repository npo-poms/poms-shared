package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.i18n.Displayable;
import nl.vpro.i18n.Dutch;

import static nl.vpro.domain.Changeables.instant;

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
        return new StreamingStatusImpl(Value.UNSET, Value.UNSET, Value.UNSET);
    }

    static StreamingStatusImpl withDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(Value.ONLINE, existing.getWithoutDrm(), existing.getAudioWithoutDrm());
    }

    static StreamingStatusImpl withAudio(StreamingStatus existing) {
        return new StreamingStatusImpl(existing.getWithDrm(), existing.getWithoutDrm(), Value.ONLINE);
    }

    static StreamingStatusImpl withoutDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(existing.getWithDrm(), Value.ONLINE, existing.getAudioWithoutDrm());
    }

    static StreamingStatusImpl withAndWithoutDrm() {
        return new StreamingStatusImpl(Value.ONLINE, Value.ONLINE, Value.UNSET);
    }

    static StreamingStatusImpl offline() {
        return new StreamingStatusImpl(Value.OFFLINE, Value.OFFLINE, Value.OFFLINE);
    }

    static StreamingStatusImpl offlineDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(Value.OFFLINE, existing.getWithoutDrm(), existing.getAudioWithoutDrm());
    }

    static StreamingStatusImpl offlineWithoutDrm(StreamingStatus existing) {
        return new StreamingStatusImpl(existing.getWithDrm(), Value.OFFLINE, existing.getAudioWithoutDrm());
    }

    static List<StreamingStatusImpl> availableStatuses() {
        return Arrays.asList(
            withDrm(offline()),
            withDrm(unset()),
            withoutDrm(offline()),
            withoutDrm(unset()),
            withAndWithoutDrm(),
            StreamingStatus.withAudio(unset())
        );
    }

    static List<StreamingStatusImpl> notAvailableStatuses() {
        return Arrays.asList(
            unset(),
            offline()
        );
    }


    default ReadonlyStreamingStatus copy() {
        return new ReadonlyStreamingStatus(getWithDrm(), getWithDrmOffline(),  getWithoutDrm(), getWithoutDrmOffline(), getAudioWithoutDrm());
    }


    Value getWithDrm();

    Value getWithoutDrm();

    Instant getWithDrmOffline();

    Instant getWithoutDrmOffline();

    Value getAudioWithoutDrm();

    @NonNull
    static StreamingStatus copy(StreamingStatus of) {
        return of == null ? unset() : of.copy();
    }


    default boolean hasDrm() {
        return getWithDrm() == Value.ONLINE;
    }

    default boolean onDvrWithDrm() {
        Instant withDrmOffline = getWithDrmOffline();
        return hasDrm() && withDrmOffline != null && withDrmOffline.isAfter(instant());
    }

    default boolean hasWithoutDrm() {
        return getWithoutDrm() == Value.ONLINE;
    }

    default boolean hasAudio() {
        return getAudioWithoutDrm() == Value.ONLINE;
    }

    /**
     * @since 7.7
     */
    default boolean hasPublishedVideo() {
        return (hasDrm() && online(getWithDrmOffline())) || (hasWithoutDrm() && online(getWithoutDrmOffline()));
    }

    /**
     * @since 7.7
     */
    default boolean hasVideo() {
        return hasDrm() || hasWithoutDrm();
    }


    default boolean isAvailable() {
        return hasVideo() || hasAudio();
    }

    static boolean online(Instant offline) {
        return offline == null || offline.isAfter(instant());
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
     * Matches with configured encryption in a {@link Prediction}
     */
    default boolean matches(@Nullable Encryption encryption) {
        return
            (encryption == null && (hasAudio() || hasVideo())) || // no explitit
                (hasDrm()) ||
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
                connector = " en ";
            }
            builder.append(postFix);

            if (hasAudio()) {
                builder.append(connector).append("op audio CDN");
            }
        } else {
            builder.append("Niet beschikbaar");
        }
        return builder.toString();


    }

    /**
     * See <a href="https://wiki.vpro.nl/display/poms/Locations+and+predictions#Locationsandpredictions-Locations,streamingplatformandpredictions">wiki</a>
     * <p>
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
            case OFFLINE, UNSET -> {
                switch (getWithoutDrm()) {
                    case OFFLINE, UNSET -> {
                        return Arrays.asList();
                    }
                    case ONLINE -> {
                        return switch (e) {
                            case DRM -> Arrays.asList();
                            case NONE -> Arrays.asList(
                                Encryption.NONE);
                        };
                    }
                }
            }
            case ONLINE -> {
                switch (getWithoutDrm()) {
                    case OFFLINE, UNSET -> {
                        return Arrays.asList(Encryption.DRM);
                    }
                    case ONLINE -> {
                        return switch (e) {
                            case DRM -> Arrays.asList(Encryption.DRM);
                            case NONE -> Arrays.asList(Encryption.DRM, Encryption.NONE);
                        };
                    }
                }
            }
        }
        throw new IllegalStateException();

    }

    /**
     * Never coming in anymore
     */
    @Deprecated
    default Instant getOffline(boolean drm) {
        if (drm) {
            return getWithDrmOffline();
        } else {
            return getWithoutDrmOffline();
        }
    }

    default Optional<AVType> expectedAVType() {
        if (getWithDrm() == Value.ONLINE || getWithoutDrm() == Value.ONLINE) {
            if (getAudioWithoutDrm() == Value.ONLINE) {
                return Optional.of(AVType.MIXED);
            } else {
                return Optional.of(AVType.VIDEO);
            }
        } else if (getAudioWithoutDrm() == Value.ONLINE) {
            return Optional.of(AVType.AUDIO);
        } else {
            return Optional.empty();
        }
    }

}
