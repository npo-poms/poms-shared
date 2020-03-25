package nl.vpro.domain;

import java.time.Instant;

import com.google.common.collect.Range;

/**
 * An object having or defining a publication embargo, meaning that it has publish start and stop instants.
 *
 * Utilities are provided in {@link Embargos}.
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface Embargo {

    Instant getPublishStartInstant();

    Instant getPublishStopInstant();

    /**
     * Returns this embargo object as a guava {@link Range} object.
     */
    default Range<Instant> asRange() {
        if (getPublishStopInstant() == null) {
            if (getPublishStartInstant() == null) {
                return Range.all();
            } else {
                return Range.atLeast(getPublishStartInstant());
            }
        } else {
            if (getPublishStartInstant() != null) {
                return Range.closedOpen(getPublishStartInstant(), getPublishStopInstant());
            } else {
                return Range.lessThan(getPublishStopInstant());
            }
        }

    }

    default boolean isUnderEmbargo(Instant now) {
        return !inPublicationWindow(now);
    }

    default boolean isUnderEmbargo() {
        return isUnderEmbargo(Instant.now());
    }

    default boolean wasUnderEmbargo() {
        Instant stop = getPublishStopInstant();
        return stop != null && stop.isBefore(Instant.now());
    }

    default boolean willBeUnderEmbargo() {
        return willBeUnderEmbargo(Instant.now());
    }


    default boolean willBeUnderEmbargo(Instant now) {
        Instant start = getPublishStartInstant();
        Instant stop = getPublishStopInstant();
        return (start != null && start.isAfter(now)) || (stop != null && stop.isAfter(now));
    }
    default boolean willBePublished() {
        return isUnderEmbargo() && getPublishStopInstant().isAfter(Instant.now());
    }

    default boolean inPublicationWindow(Instant now) {
        Instant stop = getPublishStopInstant();
        if (stop != null && ! now.isBefore(stop)) {
            return false;
        }
        Instant start = getPublishStartInstant();
        if (start != null && start.isAfter(now)) {
            return false;
        }

        return true;
    }


}
