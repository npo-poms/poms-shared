/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

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
public class DateRangeInterval extends AbstractTemporalRangeInterval<Instant> {


    public DateRangeInterval() {
    }

    public DateRangeInterval(String interval) {
        super(interval);
    }

    @Override
    public boolean matches(Instant begin, Instant end) {
        Interval parsed = parsed();
        return parsed.isBucketBegin(begin)
                && parsed.getBucketEnd(begin).equals(end);
    }

}

