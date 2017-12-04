package nl.vpro.domain.api;

import java.time.temporal.ChronoField;

/**
* @author Michiel Meeuwissen
* @since 5.5
*/
public enum IntervalUnit {

    YEAR(ChronoField.YEAR, "yyyy"),
    MONTH(ChronoField.MONTH_OF_YEAR, "yyyy-MM"),
    WEEK(ChronoField.ALIGNED_WEEK_OF_YEAR, "yyyy-'W'w"),
    DAY(ChronoField.DAY_OF_YEAR, "yyyy-MM-dd"),
    HOUR(ChronoField.HOUR_OF_DAY, "yyyy-MM-dd'T'HH:mm:ss'Z'"),
    MINUTE(ChronoField.MINUTE_OF_DAY, "yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final ChronoField chronoField;

    private final String format;

    IntervalUnit(ChronoField chronoField, String format) {
        this.chronoField = chronoField;
        this.format = format;
    }


    public ChronoField getChronoField() {
        return chronoField;
    }

    public String getFormat() {
        return format;
    }
}
