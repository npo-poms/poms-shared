/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
@XmlType(name = "dateRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.FIELD)
public class DateRangeInterval implements RangeFacet<Instant> {


    @Getter
    @Setter
    private Interval interval;

    public DateRangeInterval() {
    }

    public DateRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }


    @Override
    public boolean matches(Instant begin, Instant end) {
        return interval.isBucketBegin(begin) && interval.isBucketEnd(end);
    }


    public static class Interval extends ParsedInterval<Instant> {

        public Interval(ParseResult pair) {
            super(pair);
        }

        @Override
        public boolean isBucketBegin(Instant begin) {
            begin.get(getUnit().getChronoField());
            return false;

        }

        @Override
        public boolean isBucketEnd(Instant end) {
            return false;

        }
    }


}

