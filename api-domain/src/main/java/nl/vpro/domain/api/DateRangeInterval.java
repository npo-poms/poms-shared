/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Date;

import nl.vpro.i18n.Locales;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
@XmlType(name = "dateRangeIntervalType", propOrder = {
    "interval"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DateRangeInterval extends AbstractTemporalRangeInterval<Date> {


    public DateRangeInterval() {
    }

    public DateRangeInterval(String interval) {
        super(interval);
    }

    @Override
    public boolean matches(Date begin, Date end) {
        Interval parsed = parsed();
        return parsed.isBucketBegin(begin.toInstant())
                && parsed.getBucketEnd(begin.toInstant()).equals(end.toInstant());
    }

}

