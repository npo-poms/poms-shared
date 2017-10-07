/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.temporal.Temporal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "abstractRangeIntervalType", propOrder = {
    "interval"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractTemporalRangeInterval<T extends Comparable<T> & Temporal> implements RangeFacet<T> {



    @XmlValue
    @Getter
    @Setter
    private Interval interval;

    public AbstractTemporalRangeInterval() {
    }

    public AbstractTemporalRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }

    public abstract Interval parsed();


    @Override
    public boolean matches(T begin, T end) {
        return parsed().isBucketBegin(begin) && parsed().isBucketEnd(end);
    }


    public class Interval extends ParsedInterval<T> {

        public Interval(ParseResult pair) {
            super(pair);
        }

        @Override
        public boolean isBucketBegin(T begin) {
            begin.get(getUnit().getChronoField());
            return false;

        }

        @Override
        public boolean isBucketEnd(T end) {
            return false;

        }
    }


}

