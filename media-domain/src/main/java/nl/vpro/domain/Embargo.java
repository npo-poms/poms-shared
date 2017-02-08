package nl.vpro.domain;

import java.time.Duration;
import java.time.Instant;

/**
 * An object describing a publication embargo, meaning that it has a publish start and stop instant.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo {


    Instant getEmbargoStart();

    Embargo setEmbargoStart(Instant publishStart);

    Instant getEmbargoStop();

    Embargo setEmbargoStop(Instant publishStop);


    default boolean isUnderEmbargo() {
        return ! isInAllowedPublicationWindow();
    }

    default boolean wasUnderEmbargo() {
        Instant stop = getEmbargoStop();
        return stop != null && stop.isBefore(Instant.now());
    }

    default boolean willBeUnderEmbargo() {
        Instant start = getEmbargoStart();
        return start != null && start.isAfter(Instant.now());
    }

    default boolean isInAllowedPublicationWindow() {
        return isInAllowedPublicationWindow(Duration.ZERO);
    }

    default boolean isInAllowedPublicationWindow(java.time.Duration fromNow) {
        Instant stop = getEmbargoStop();
        if (stop != null
            && stop.isBefore(Instant.now().plus(fromNow))) {

            return false;
        }
        Instant start = getEmbargoStart();
        if (start != null
            && start.isAfter(Instant.now().plus(fromNow))) {

            return false;
        }

        return true;
    }

}
