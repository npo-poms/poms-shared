package nl.vpro.domain.media.search;

import java.util.function.Predicate;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface Range<T extends Comparable> extends Predicate<T> {

    T getStart();
    void setStart(T start);
    T getStop();
    void setStop(T  stop);
    @Override
    default boolean test(T other) {
        T start = getStart();
        T stop = getStop();
        return (start == null || start.compareTo(other) <= 0) && (stop == null || stop.compareTo(other) > 0);
    }
    default boolean hasValues() {
        return getStart() != null || getStop() != null;

    }
}
