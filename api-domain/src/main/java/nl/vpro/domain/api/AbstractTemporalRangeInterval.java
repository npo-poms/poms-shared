/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.i18n.Locales;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "abstractRangeIntervalType", propOrder = {
    "interval"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractTemporalRangeInterval<T extends Comparable<T>> implements RangeFacet<T> {

    public static final String DATERANGE_PATTERN = "(\\d+)?\\s*(YEAR|MONTH|WEEK|DAY|HOUR|MINUTE)S?";

    public static final String TIMEZONE = "CET";

    public static final ZoneId ZONE = ZoneId.of(TIMEZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");


    @XmlValue
    @Pattern(regexp = DATERANGE_PATTERN)
    private String interval;

    public AbstractTemporalRangeInterval() {
    }

    public AbstractTemporalRangeInterval(String interval) {
        this.interval = interval;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Interval parsed() {
        return Interval.parse(this.interval);
    }

    @Override
    public abstract boolean matches(T begin, T end);

    public static class Interval {

        final AbstractTemporalRangeInterval.Unit unit;

        final int number;

        Interval(AbstractTemporalRangeInterval.Unit unit, int number) {
            this.unit = unit;
            this.number = number;
        }

        Instant getBucketEnd(Instant beginDate) {
            ZonedDateTime begin = ZonedDateTime.ofInstant(beginDate, ZONE);
            switch (unit) {
                case YEAR:
                    return begin.plusYears(number).toInstant();
                case MONTH:
                    return begin.plusMonths(number).toInstant();
                case WEEK:
                    return begin.plusWeeks(number).toInstant();
                case DAY:
                    return begin.plusDays(number).toInstant();
                case HOUR:
                    return begin.plusHours(number).toInstant();
                case MINUTE:
                    return begin.plusMinutes(number).toInstant();
                default:
                    throw new IllegalArgumentException();
            }
        }
        boolean isBucketBegin(Instant beginDate) {
            ZonedDateTime date = ZonedDateTime.ofInstant(beginDate, ZoneId.of("GMT"));
            return unit.atBegin(date.toInstant());
            // This is hard, and probably needs details of implementation of ES:  && (joda.get(unit.getJodaField()) + 1) % number == 0;
        }

        static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(AbstractTemporalRangeInterval.DATERANGE_PATTERN);

        static Interval parse(String input) {
            java.util.regex.Matcher matcher = PATTERN.matcher(input.toUpperCase());

            if (!matcher.matches()) {
                throw new IllegalArgumentException(input);
            }
            final int number = matcher.group(1) == null ? 1 : Integer.valueOf(matcher.group(1));
            final AbstractTemporalRangeInterval.Unit unit = AbstractTemporalRangeInterval.Unit.valueOf(matcher.group(2));
            return new Interval(unit, number);
        }


        public String print(Instant dateTime, boolean asDuration) {
            return unit.print(dateTime, asDuration);
        }
    }


    enum Unit {

        YEAR {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                return asDuration
                    ? sign(dateTime, "P" + Period.between(Instant.EPOCH.atZone(GMT).toLocalDate(), dateTime.atZone(GMT).toLocalDate()).getYears() + "Y")
                    : String.valueOf(dateTime.atZone(ZONE).getYear());
            }

            @Override
            public ChronoField getChronoField() {
                return ChronoField.YEAR;

            }

            @Override
            public boolean atBegin(Instant dateTime) {
                return get(dateTime).getDayOfYear() == 1 && dateTime.atZone(GMT).toLocalTime().toNanoOfDay() == 0;
            }
        },
        MONTH {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                return asDuration
                    ? sign(dateTime, "P" + Period.between(Instant.EPOCH.atZone(GMT).toLocalDate(), dateTime.atZone(GMT).toLocalDate()).toTotalMonths()) + "M"
                    : String.format("%04d-%02d", dateTime.atZone(ZONE).getYear(), dateTime.atZone(ZONE).getMonthValue());
            }
            @Override
            public ChronoField getChronoField() {
                return ChronoField.MONTH_OF_YEAR;

            }

            @Override
            public boolean atBegin(Instant dateTime) {
                return get(dateTime).getDayOfMonth() == 1 && get(dateTime).toLocalTime().toNanoOfDay() == 0;

            }
        },
        WEEK {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                WeekFields weekFields = WeekFields.of(Locales.DUTCH);
                return asDuration
                    ? sign(dateTime, "P" + ChronoUnit.WEEKS.between(Instant.EPOCH.atZone(GMT).toLocalDate(), dateTime.atZone(GMT).toLocalDate()) + "W")
                    : String.format("%04d-W%02d", dateTime.atZone(ZONE).getYear(), dateTime.atZone(ZONE).get(weekFields.weekOfYear()));
            }

            @Override
            public String getShortEs() {
                return "w";
            }

            @Override
            public ChronoField getChronoField() {
                return ChronoField.ALIGNED_WEEK_OF_YEAR;
            }

            @Override
            public boolean atBegin(Instant dateTime) {
                return dateTime.atZone(GMT).toLocalTime().toNanoOfDay() == 0
                        && get(dateTime).toLocalDate().getDayOfWeek() == DayOfWeek.THURSDAY; // THURSDAY, why?

            }
        },
        DAY {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                return asDuration
                    ? sign(dateTime, "P" + ChronoUnit.DAYS.between(Instant.EPOCH.atZone(GMT).toLocalDate(), dateTime.atZone(GMT).toLocalDate()) + "D")
                    : dateTime.atZone(ZONE).toLocalDate().toString();
            }

            @Override
            public String getShortEs() {
                return "d";
            }

            @Override
            public ChronoField getChronoField() {
                return ChronoField.DAY_OF_YEAR;
            }

            @Override
            public boolean atBegin(Instant dateTime) {
                return dateTime.atZone(ZONE).toLocalTime().toNanoOfDay() == 0;

            }
        },
        HOUR {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                return asDuration
                    ? sign(dateTime, "PT" + ChronoUnit.HOURS.between(Instant.EPOCH.atZone(GMT).toLocalDateTime(), dateTime.atZone(GMT).toLocalDateTime()) + "H")
                    : ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));
            }

            @Override
            public String getShortEs() {
                return "h";
            }

            @Override
            public ChronoField getChronoField() {
                return ChronoField.HOUR_OF_DAY;

            }

            @Override
            public boolean atBegin(Instant dateTime) {
                return get(dateTime).toLocalTime().getMinute() == 0 &&
                        get(dateTime).toLocalTime().getSecond() == 0 &&
                        get(dateTime).toLocalTime().getNano() == 0;

            }
        },
        MINUTE {
            @Override
            public String print(Instant dateTime, boolean asDuration) {
                return asDuration
                    ? sign(dateTime, "PT" + ChronoUnit.MINUTES.between(Instant.EPOCH.atZone(GMT).toLocalDateTime(), dateTime.atZone(GMT).toLocalDateTime()) + "M")
                    : ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));

            }

            @Override
            public String getShortEs() {
                return "m";
            }

            @Override
            public ChronoField getChronoField() {
                return ChronoField.MINUTE_OF_DAY;

            }

            @Override
            public boolean atBegin(Instant  dateTime) {
                return dateTime.atZone(ZONE).getSecond() == 0 &&
                        dateTime.atZone(ZONE).toLocalTime().getNano() == 0;

            }
        };

        private static ZonedDateTime get(Instant instant) {
            return instant.atZone(ZONE);
        }

        private static long correctedAbs(Instant dateTime) {
            return Math.abs(dateTime.toEpochMilli());
        }

        public String print(Instant dateTime, boolean asDuration) {
            throw new UnsupportedOperationException("No unit available for " + this);
        }

        public String getShortEs() {
            throw new UnsupportedOperationException("No multiples available for  " + this);
        }


        public abstract ChronoField getChronoField();

        public abstract boolean atBegin(Instant dateTime);

        private static String sign(Instant dateTime, String positiveDuration) {
            return dateTime.toEpochMilli() < 0 ? '-' + positiveDuration : positiveDuration;
        }
    }
}

