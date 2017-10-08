package nl.vpro.domain.api;

import java.time.temporal.ChronoField;

/**
* @author Michiel Meeuwissen
* @since 5.5
*/
public enum IntervalUnit {

    YEAR(ChronoField.YEAR),
    MONTH(ChronoField.MONTH_OF_YEAR),
    WEEK(ChronoField.ALIGNED_WEEK_OF_YEAR),
    DAY(ChronoField.DAY_OF_YEAR),
    HOUR(ChronoField.HOUR_OF_DAY),
    MINUTE(ChronoField.MINUTE_OF_DAY);

    private final ChronoField chronoField;

    IntervalUnit(ChronoField chronoField) {
        this.chronoField = chronoField;
    }


    public ChronoField getChronoField() {
        return chronoField;
    }
}
