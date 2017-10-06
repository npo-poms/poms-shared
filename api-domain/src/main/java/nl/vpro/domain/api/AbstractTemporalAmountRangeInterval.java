/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.temporal.TemporalAmount;

import javax.validation.constraints.Pattern;
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
public abstract class AbstractTemporalAmountRangeInterval<T extends TemporalAmount & Comparable<T>> implements RangeFacet<T> {

    public static final String TEMPORAL_AMOUNT_INTERVAL = "(\\d+)?\\s*(YEAR|MONTH|WEEK|DAY|HOUR|MINUTE)S?";


    @XmlValue
    @Pattern(regexp = TEMPORAL_AMOUNT_INTERVAL)
    @Getter
    @Setter
    private String interval;

    public AbstractTemporalAmountRangeInterval() {
    }

    public AbstractTemporalAmountRangeInterval(String interval) {
        this.interval = interval;
    }

    public abstract Interval parsed();

    @Override
    public boolean matches(T begin, T end) {
        Interval parsed = parsed();
        return parsed.isBucketBegin(begin)
            && parsed.isBucketEnd(end);
    }


    public class Interval extends ParsedInterval<T> {

        public Interval(int amount, Unit unit) {
            super(amount, unit);
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

