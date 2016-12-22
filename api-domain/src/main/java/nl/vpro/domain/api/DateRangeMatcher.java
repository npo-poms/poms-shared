/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.util.DateUtils;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "dateRangeMatcherType", propOrder = {"begin", "end"})
public class DateRangeMatcher extends RangeMatcher<Date> {

    public DateRangeMatcher() {
    }

    public DateRangeMatcher(Date begin, Date end) {
        super(begin, end);
    }

    public DateRangeMatcher(Date begin, Date end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    public DateRangeMatcher(Instant begin, Instant end, Boolean inclusiveEnd) {
        super(DateUtils.toDate(begin), DateUtils.toDate(end), inclusiveEnd);
    }

    public DateRangeMatcher(Instant begin, Instant end) {
        super(DateUtils.toDate(begin), DateUtils.toDate(end));
    }

    /**
     * TODO Makes no sense. Durations are not dates.
     */
    @Deprecated
    public DateRangeMatcher(Duration begin, Duration end, Boolean inclusiveEnd) {
        super(DateUtils.toDate(begin), DateUtils.toDate(end), inclusiveEnd);
    }

    public DateRangeMatcher(Date begin, Date end, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd, match);
    }

    @XmlElement
    @Override
    public Date getBegin() {
        return begin;
    }

    @Override
    public void setBegin(Date begin) {
        this.begin = begin;
    }

    @XmlElement
    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public void setEnd(Date end) {
        this.end = end;
    }
}
