/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.util.DateUtils;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "dateRangeMatcherType", propOrder = {"begin", "end"})
public class DateRangeMatcher extends RangeMatcher<Date> implements Predicate<Date> {

    @XmlElement
    @Getter
    @Setter
    private Date begin;
    @XmlElement
    @Getter
    @Setter
    private Date end;


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

    @Override
    public boolean test(Date date) {
        return super.testComparable(date);

    }
}
