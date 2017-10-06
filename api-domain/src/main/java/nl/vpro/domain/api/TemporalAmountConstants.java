package nl.vpro.domain.api;

import lombok.Getter;

import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class TemporalAmountConstants {

    public static final String TIMEZONE = "CET";

    public static final ZoneId ZONE = ZoneId.of(TIMEZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");
}


    enum Unit {

        YEAR(ChronoField.YEAR),
        MONTH(ChronoField.MONTH_OF_YEAR),
        WEEK(ChronoField.ALIGNED_WEEK_OF_YEAR),
        DAY(ChronoField.DAY_OF_YEAR),
        HOUR(ChronoField.HOUR_OF_DAY),
        MINUTE(ChronoField.MINUTE_OF_DAY);

        @Getter
        private final ChronoField chronoField;

        Unit(ChronoField chronoField) {
            this.chronoField = chronoField;
        }


    }
