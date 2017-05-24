/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Duration;
import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeIntervalType", propOrder = {
    "interval"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DurationRangeInterval extends AbstractTemporalRangeInterval<Duration> {


    public DurationRangeInterval() {
    }

    public DurationRangeInterval(String interval) {
        super(interval);
    }

    @Override
    public boolean matches(Duration begin, Duration end) {
        Interval parsed = parsed();
        return parsed.isBucketBegin(Instant.ofEpochMilli(begin.toMillis()))
            && parsed.getBucketEnd(Instant.ofEpochMilli(begin.toMillis())).equals(Instant.ofEpochMilli(end.toMillis()))
            ;
    }

}

