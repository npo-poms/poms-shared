/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import java.util.Objects;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "integerRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")

public class IntegerRange implements Range<Long, IntegerRange.Value> {

    @XmlElement
    private Value start;

    @XmlElement
    private Value stop;

    public IntegerRange() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerRange that = (IntegerRange) o;

        if (!Objects.equals(start, that.start)) return false;
        return Objects.equals(stop, that.stop);
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        return result;
    }

    public static IntegerRange equals(long value) {
        return builder().equals(value).build();
    }

    public static IntegerRange zero() {
        return builder().equals(0L).build();
    }
    public static IntegerRange gte(long start) {
        return builder().start(start).build();
    }


    public static class Builder {

        /**
         * Adds (inclusive) start value.
         */
        public Builder start(Long start) {
            return start(start, true);
        }

        /**
         * Adds (exclusive) stop value.
         */
        public Builder stop(Long stop) {
            return stop(stop, false);
        }

        public Builder start(Long start, boolean inclusive) {
            this.start = Value.builder().value(start)
                .inclusive(inclusive)
                .build();
            return this;
        }

        public Builder stop(Long stop, boolean inclusive) {
            this.stop = Value.builder().value(stop).inclusive(inclusive).build();
            return this;
        }


        public Builder equals(Long stopandstart) {
            return stop(stopandstart, true).start(stopandstart, true);
        }

        public Builder start(Value start) {
            this.start = start;
            return this;
        }

        public Builder stop(Value stop) {
            this.stop = stop;
            return this;
        }
    }



    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "integerRangeValueType")
    public static class Value extends Range.RangeValue<Long> {

        @XmlValue
        Long value;

        public Value() {

        }
        @lombok.Builder
        public Value(Boolean inclusive, Long value) {
            super(inclusive);
            this.value = value;
        }

        public static Value of(Long instant) {
            return builder().value(instant).build();
        }

        public static Value exclusive(Long instant) {
             return builder()
                 .value(instant)
                 .inclusive(false)
                 .build();
        }
    }

}
