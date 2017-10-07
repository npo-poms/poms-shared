/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.temporal.TemporalAmount;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.util.Pair;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "abstractRangeIntervalType", propOrder = {
    "interval"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractTemporalAmountRangeInterval<T extends TemporalAmount & Comparable<T>> implements RangeFacet<T> {


    @XmlValue
    @Getter
    @Setter
    private Interval interval;

    public AbstractTemporalAmountRangeInterval() {
    }

    public AbstractTemporalAmountRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }


    @Override
    public boolean matches(T begin, T end) {
        Interval parsed = getInterval();
        return parsed.isBucketBegin(begin)
            && parsed.isBucketEnd(end);
    }


    public class Interval extends ParsedInterval<T> {

        public Interval(ParseResult pair) {
            super(pair);
        }

        @Override
        public boolean isBucketBegin(T begin) {
            //return begin != null && begin.get(getUnit().getChronoField().getRangeUnit());
            return false;

        }

        @Override
        public boolean isBucketEnd(T end) {
            return false;

        }
    }


}

