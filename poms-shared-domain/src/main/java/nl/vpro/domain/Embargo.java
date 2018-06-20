package nl.vpro.domain;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Range;

/**
 * Also includes setters.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Embargo<T extends Embargo<T>> extends ReadonlyEmbargo {

    @Nonnull
    T setPublishStartInstant(@Nullable Instant publishStart);

    @Nonnull
    T setPublishStopInstant(@Nullable Instant publishStop);

    default T set(Range<Instant> range) {
        if (range.hasLowerBound()) {
            setPublishStartInstant(range.lowerEndpoint());
        } else {
            setPublishStartInstant(null);
        }
        if (range.hasUpperBound()) {
            setPublishStopInstant(range.upperEndpoint());
        } else {
            setPublishStopInstant(null);
        }
        return (T) this;
    }



}
