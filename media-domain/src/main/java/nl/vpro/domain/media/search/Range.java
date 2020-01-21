package nl.vpro.domain.media.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface Range<T extends Comparable<T>, S extends Range.RangeValue<T>> extends Predicate<T> {

    S getStart();
    void setStart(S start);

    S getStop();
    void setStop(S  stop);

    default T getStartValue() {
        S start = getStart();
        return start == null ? null : start.get();
    }

    default T getStopValue() {
        S stop= getStop();
        return stop == null ? null : stop.get();
    }

    @Override
    default boolean test(T other) {
        return testStart(other) && testStop(other);
    }
    default boolean testStart(T other) {
        S  start = getStart();
        if (start == null || start.get() == null) {
            return true;
        }
        if (start.isInclusive()) {
            return start.get().compareTo(other) <= 0;
        } else {
            return start.get().compareTo(other) < 0;
        }
    }

    default boolean testStop(T other) {
        S stop = getStop();
        if (stop == null || stop.get() == null) {
            return true;
        }
        if (stop.isInclusive()) {
            return stop.get().compareTo(other) >= 0;
        } else {
            return stop.get().compareTo(other) > 0;
        }
    }
    default boolean hasValues() {
        return getStartValue() != null || getStopValue() != null;
    }

    @Data
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.NONE)
    @XmlTransient
    abstract class RangeValue<T extends Comparable<T>> implements Supplier<T> {
        @XmlAttribute
        Boolean inclusive = null;

        RangeValue() {

        }

        public boolean isInclusive() {
            return inclusive == null || inclusive;
        }
        @Override
        public T get() {
            return getValue();
        }
        public abstract T getValue();
    }
}
