/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.FIELD)
public class DurationRangeInterval implements RangeFacet<Duration> {


    @XmlValue
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
        return parsed.isBucketBegin(begin)
            && parsed.isBucketEnd(end);
    }

    public static class Interval extends ParsedInterval<Duration> {

        public Interval(ParseResult pair) {
            super(pair);
        }

        @Override
        public boolean isBucketBegin(Duration begin) {
            //return begin != null && begin.get(getUnit().getChronoField().getRangeUnit());
            return false;

        }

        @Override
        public boolean isBucketEnd(Duration end) {
            return false;

        }
    }

}

