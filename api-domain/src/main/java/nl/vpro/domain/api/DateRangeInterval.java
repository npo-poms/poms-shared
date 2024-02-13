/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

import nl.vpro.domain.media.Schedule;
import nl.vpro.i18n.Locales;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static nl.vpro.domain.api.ParsedInterval.TEMPORAL_AMOUNT_INTERVAL;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
@XmlType(name = "dateRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class DateRangeInterval implements RangeFacet<Instant> {

    public static final String TIMEZONE = "CET";

    public static final ZoneId ZONE = ZoneId.of(TIMEZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");


    @Getter
    @Setter
    private Interval interval;

    public DateRangeInterval() {
    }

    public DateRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }

    public DateRangeInterval(int amount, IntervalUnit unit) {
        this.interval = new Interval(amount, unit);
    }


    @Override
    public boolean matches(Instant begin, Instant end) {

        return
            begin != null && end != null
                //values do not match exactly but approximately. So .equals() method replaced by .approximatelyEquals() with a 1 percent treshold
                && approximatelyEquals(Duration.between(begin, end).toMillis(), interval.getDuration().toMillis(), 1L)
                && interval.isBucketBegin(begin)
                && interval.isBucketEnd(end);
    }


    public boolean approximatelyEquals(Long desiredValue, Long actualValue, Long tolerancePercentage) {
        long diff = Math.abs(desiredValue - actualValue);
        double tolerance = (tolerancePercentage * 0.01) * desiredValue;
        return diff < tolerance;
    }


    @XmlValue
    @JsonValue
    protected String getIntervalString() {
        return interval.getValue();
    }

    @jakarta.validation.constraints.Pattern(regexp = TEMPORAL_AMOUNT_INTERVAL)
    protected void setIntervalString(String value) {
        this.interval = new Interval(ParsedInterval.parse(value));
    }

    @Override
    public String toString() {
        return getIntervalString();
    }



    public static class Interval extends ParsedInterval<Instant> {


        public Interval(int amount, IntervalUnit unit) {
            super(amount, unit);
        }
        public Interval(ParseResult pair) {
            super(pair);
        }

        protected ZonedDateTime getZoned(Instant instant) {
            return instant.atZone(Schedule.ZONE_ID);
        }
        protected ZonedDateTime truncated(ZonedDateTime zoned) {
            ZonedDateTime truncated;
            if (getUnit().getChronoField().compareTo(ChronoField.SECOND_OF_DAY) > 0) {
                truncated = zoned.truncatedTo(ChronoField.SECOND_OF_DAY.getBaseUnit());

            } else {
                truncated = zoned.truncatedTo(getUnit().getChronoField().getBaseUnit());
            }
            return truncated;
        }
        protected int getRelevantFieldValue(ZonedDateTime zoned) {
            return zoned.get(getUnit().getChronoField());
        }

        @Override
        public boolean isBucketBegin(Instant begin) {
            ZonedDateTime zoned = getZoned(begin);
            return getRelevantFieldValue(zoned) % amount == 0
                && zoned.equals(truncated(zoned));
        }

        @Override
        public boolean isBucketEnd(Instant end) {
            ZonedDateTime zoned = getZoned(end);
            return getRelevantFieldValue(zoned) % amount == 0
                && zoned.equals(truncated(zoned));

        }

        private static final WeekFields WEEK_FIELDS = WeekFields.of(Locales.NETHERLANDISH);

        @Override
        public String print(Instant dateTime) {

            return switch (unit) {
                case YEAR -> String.valueOf(dateTime.atZone(ZONE).getYear());
                case MONTH ->
                    String.format("%04d-%02d", dateTime.atZone(ZONE).getYear(), dateTime.atZone(ZONE).getMonthValue());
                case WEEK -> String.format("%04d-W%02d",
                    dateTime.atZone(ZONE).get(WEEK_FIELDS.weekBasedYear()),
                    dateTime.atZone(ZONE).get(WEEK_FIELDS.weekOfWeekBasedYear())
                );
                case DAY -> dateTime.atZone(ZONE).toLocalDate().toString();
                case HOUR -> ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));
                case MINUTE -> ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));
                default -> throw new IllegalArgumentException();
            };

        }

    }


}

