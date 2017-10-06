/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

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
public class DurationRangeInterval extends AbstractTemporalAmountRangeInterval<Duration> {


    public DurationRangeInterval() {
    }

    public DurationRangeInterval(String interval) {
        super(interval);
    }

    @Override
    public Interval parsed() {
        return new Interval(1, Unit.HOUR);
    }


}

