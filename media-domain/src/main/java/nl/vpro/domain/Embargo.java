package nl.vpro.domain;

import java.time.Instant;

/**
 * An object having or defining a publication embargo, meaning that it has publish start and stop instants.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo extends ReadonlyEmbargo {

    Embargo setPublishStartInstant(Instant publishStart);

    Embargo setPublishStopInstant(Instant publishStop);
    
}
