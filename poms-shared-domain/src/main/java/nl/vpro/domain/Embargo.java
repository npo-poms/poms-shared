package nl.vpro.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Range;

import nl.vpro.util.Ranges;

import static nl.vpro.domain.Changeables.instant;

/**
 * An object having or defining a publication embargo, meaning that it has {@link #getPublishStartInstant()  publish} start and {@link #getPublishStopInstant() stop} {@link Instant instants}.
 * <p>
 * Utilities are provided in {@link Embargos}.
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 * @see MutableEmbargo
 */
public interface Embargo {

    Instant getPublishStartInstant();

    Instant getPublishStopInstant();

    /**
     * Returns this embargo object as a guava {@link Range} object.
     */
    default Range<Instant> asRange() {
        return Ranges.closedOpen(getPublishStartInstant(), getPublishStopInstant());
    }

    default boolean isUnderEmbargo(Instant now) {
        return !inPublicationWindow(now);
    }

    @JsonIgnore
    default boolean isUnderEmbargo() {
        return isUnderEmbargo(instant());
    }

    default boolean wasUnderEmbargo() {
        return wasUnderEmbargo(instant());
    }

    default boolean wasUnderEmbargo(Instant now) {
        Instant stop = getPublishStopInstant();
        return stop != null && ! now.isBefore(stop);
    }


    /**
     * Is now published, but will not anymore be at some point in the future
     */
    default boolean willBeUnderEmbargo() {
        return willBeUnderEmbargo(instant());
    }

    default boolean willBeUnderEmbargo(Instant now) {
        Instant stop = getPublishStopInstant();
        return inPublicationWindow(now) && (stop != null && stop.isAfter(now));
    }

    /**
     * Is now under embargo, but will not anymore be at some point in the future
     */
    default boolean willBePublished() {
        return willBePublished(instant());
    }

    default boolean willBePublished(Instant now) {
        return isConsiderableForPublication() && isUnderEmbargo(now) && (getPublishStartInstant() != null && now.isBefore(getPublishStartInstant()));
    }

    default boolean inPublicationWindow() {
        return inPublicationWindow(instant());
    }

    default boolean inPublicationWindow(Instant now) {
        Instant stop = getPublishStopInstant();
        if (stop != null && ! now.isBefore(stop)) {
            return false;
        }
        Instant start = getPublishStartInstant();
        return start == null || !start.isAfter(now);
    }

    /**
     * Whether this object is publishable.
     * This defaults to {@link #inPublicationWindow()}, but extensions may improve on this. E.g. {@code nl.vpro.domain.media.TrackableObject} also checks whether the object is deleted or not (by overriding {@link #isConsiderableForPublication()}
     */
    @JsonIgnore
    default boolean isPublishable() {
        return isConsiderableForPublication() && inPublicationWindow();
    }

    @JsonIgnore
    default boolean isConsiderableForPublication() {
        return true;
    }

}
