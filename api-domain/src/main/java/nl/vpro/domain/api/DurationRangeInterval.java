/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonValue;

import static nl.vpro.domain.api.ParsedInterval.TEMPORAL_AMOUNT_INTERVAL;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class DurationRangeInterval implements RangeFacet<Duration> {


    @Getter
    @Setter
    private Interval interval;


    public DurationRangeInterval() {
    }

    public DurationRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }


    @Override
    public boolean matches(Duration begin, Duration end) {
        Interval parsed = getInterval();
        return
            begin != null
                && end != null
                && end.toMillis() - begin.toMillis() == parsed.getDuration().toMillis()
                && parsed.isBucketBegin(begin)
                && parsed.isBucketEnd(end);
    }

    @XmlValue
    @JsonValue
    protected String getIntervalString() {
        return interval == null ? null : interval.getValue();
    }

    @jakarta.validation.constraints.Pattern(regexp = TEMPORAL_AMOUNT_INTERVAL)
    protected void setIntervalString(String value) {
        this.interval = new Interval(ParsedInterval.parse(value));
    }

    @Override
    public String toString() {
        return getIntervalString();
    }



    public static class Interval extends ParsedInterval<Duration> {

        public Interval(ParseResult pair) {
            super(pair);
        }

        @Override
        public boolean isBucketBegin(Duration begin) {
            return begin.toMillis() % getDuration().toMillis()  == 0;
        }

        @Override
        public boolean isBucketEnd(Duration end) {
            return end.toMillis()  % getDuration().toMillis() == 0;

        }

        @Override
        public String print(Duration value) {
            return value.toString();

        }
    }

}

