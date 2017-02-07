package nl.vpro.domain;

import java.time.Duration;
import java.time.Instant;

/**
 * An object describing a publication embargo, meaning that it has a publish start and stop instant.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo<T extends Embargo<T>> {


    Instant getPublishStartInstant();

    T setPublishStartInstant(Instant publishStart);

    Instant getPublishStopInstant();

    T setPublishStopInstant(Instant publishStop);


    default boolean isInAllowedPublicationWindow() {
        return isInAllowedPublicationWindow(Duration.ZERO);
    }

    default boolean isInAllowedPublicationWindow(java.time.Duration fromNow) {
        Instant stop = getPublishStopInstant();
        if (stop != null
            && stop.isBefore(Instant.now().plus(fromNow))) {

            return false;
        }
        Instant start = getPublishStartInstant();
        if (start != null
            && start.isAfter(Instant.now().plus(fromNow))) {

            return false;
        }

        return true;
    }

}
