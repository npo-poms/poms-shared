package nl.vpro.domain;

import java.time.Instant;

/**
 * Also includes setters.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo<T extends Embargo<T>> extends ReadonlyEmbargo {

    T setPublishStartInstant(Instant publishStart);

    T setPublishStopInstant(Instant publishStop);

}
