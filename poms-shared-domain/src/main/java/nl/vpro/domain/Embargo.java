package nl.vpro.domain;

import java.time.Instant;

import com.google.common.collect.Range;

/**
 * Also includes setters.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo<T extends Embargo<T>> extends ReadonlyEmbargo {

    T setPublishStartInstant(Instant publishStart);

    T setPublishStopInstant(Instant publishStop);

    default T set(Range<Instant> range) {
        setPublishStartInstant(range.lowerEndpoint());
        setPublishStopInstant(range.upperEndpoint());
        return (T) this;
    }

}
