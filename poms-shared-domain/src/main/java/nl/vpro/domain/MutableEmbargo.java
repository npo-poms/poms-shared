package nl.vpro.domain;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import com.google.common.collect.Range;

/**
 * Also includes setters.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface MutableEmbargo<T extends MutableEmbargo<T>> extends Embargo {

    @NonNull
    T setPublishStartInstant(@Nullable Instant publishStart);

    @NonNull
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
