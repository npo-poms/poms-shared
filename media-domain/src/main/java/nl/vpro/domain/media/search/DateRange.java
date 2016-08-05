/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.media.Schedule;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeType", propOrder = {
        "start",
        "stop"
        })
public class DateRange implements Predicate<Instant> {

    @XmlElement
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant start;

    @XmlElement
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant stop;

    public  DateRange() {
    }

    public DateRange(Instant  start, Instant stop) {
        this.start = start;
        this.stop = stop;
    }

    public DateRange(LocalDateTime start, LocalDateTime stop) {
        this.start = start == null ? null : start.atZone(Schedule.ZONE_ID).toInstant();
        this.stop = stop == null ? null : stop.atZone(Schedule.ZONE_ID).toInstant();
    }


    public boolean hasValues() {
        return start != null || stop != null;
    }

    /**
     * Start date time of date-range (inclusive)
     */
    public Instant getStart() {
        return start;
    }

    /**
     * End date time of date-range (exclusive)
     */
    public Instant getStop() {
        return stop;
    }

    @Override
    public boolean test(Instant date) {
        return (start == null || start.equals(date) || start.isBefore(date)) && (stop == null || stop.isAfter(date));

    }
}
