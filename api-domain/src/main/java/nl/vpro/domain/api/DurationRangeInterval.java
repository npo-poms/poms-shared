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

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.FIELD)
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
            end.toMillis() - begin.toMillis() == parsed.getDuration().toMillis()
            && parsed.isBucketBegin(begin)
            && parsed.isBucketEnd(end);
    }


    @XmlType(name = "temporalAmountIntervalType", propOrder = {
    })
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
    }

}

