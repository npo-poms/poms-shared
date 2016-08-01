/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.Schedule;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeType", propOrder = {
        "start",
        "stop"
        })
public class DateRange implements Predicate<Date> {

    @XmlElement
    private Date start;

    @XmlElement
    private Date stop;

    public  DateRange() {
    }

    public DateRange(Date start, Date stop) {
        this.start = start;
        this.stop = stop;
    }

    public DateRange(LocalDateTime start, LocalDateTime stop) {
        this.start = start == null ? null : Date.from(start.atZone(Schedule.ZONE_ID).toInstant());
        this.stop = stop == null ? null : Date.from(stop.atZone(Schedule.ZONE_ID).toInstant());
    }

    public static DateRange fromString(String start, String stop, SimpleDateFormat format) throws ParseException {
        Date startDate = (StringUtils.isNotEmpty(start)) ? format.parse(start) : null;
        Date stopDate = (StringUtils.isNotEmpty(stop)) ? format.parse(stop) : null;

        return new DateRange(startDate, stopDate);
    }

    public boolean hasValues() {
        return start != null || stop != null;
    }

    /**
     * Start date time of date-range (inclusive)
     * @return
     */
    public Date getStart() {
        return start;
    }

    /**
     * End date time of date-range (exclusive)
     * @return
     */
    public Date getStop() {
        return stop;
    }

    @Override
    public boolean test(Date date) {
        return (start == null || start.equals(date) || start.before(date)) && (stop == null || stop.after(date));

    }
}
