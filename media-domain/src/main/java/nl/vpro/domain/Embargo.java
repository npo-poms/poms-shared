package nl.vpro.domain;

import java.time.Instant;

/**
 * Also includes setters.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo extends ReadonlyEmbargo {

    Embargo setPublishStartInstant(Instant publishStart);

    Embargo setPublishStopInstant(Instant publishStop);

}
