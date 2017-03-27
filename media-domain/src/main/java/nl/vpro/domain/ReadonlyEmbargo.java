package nl.vpro.domain;

import java.time.Instant;

/**
 *  An object having or defining a publication embargo, meaning that it has publish start and stop instants.
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface ReadonlyEmbargo {

    Instant getPublishStartInstant();

    Instant getPublishStopInstant();

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
        Instant start = getPublishStartInstant();
        return start != null && start.isAfter(Instant.now());
    }

    default boolean inPublicationWindow(Instant now) {
        Instant stop = getPublishStopInstant();
        if (stop != null && stop.isBefore(now)) {
            return false;
        }
        Instant start = getPublishStartInstant();
        if (start != null && start.isAfter(now)) {
            return false;
        }

        return true;
    }

}
