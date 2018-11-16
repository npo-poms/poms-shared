package nl.vpro.i18n;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class Dutch {


    public static ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");



    private static final DateTimeFormatter FORMATTER_VERY_LONG;
    private static final DateTimeFormatter FORMATTER_LONG;
    private static final DateTimeFormatter FORMATTER_SHORT;
    static {
        FORMATTER_VERY_LONG =
            DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm", Locales.DUTCH)
                .withZone(ZONE_ID);
        FORMATTER_LONG =
            DateTimeFormatter.ofPattern("d MMMM HH:mm", Locales.DUTCH)
                .withZone(ZONE_ID);
        FORMATTER_SHORT =
            DateTimeFormatter.ofPattern("HH:mm", Locales.DUTCH)
                .withZone(ZONE_ID);
    }

    public static String formatInstantSmartly(Temporal now, Temporal instant) {

        Duration distance = Duration.between(now, instant).abs();
        if (distance.compareTo(Duration.ofHours(24)) < 0) {
            return FORMATTER_SHORT.format(instant);
        } else if (distance.compareTo(Duration.ofDays(365)) < 0) {
            return FORMATTER_LONG.format(instant);
        } else {
            return FORMATTER_VERY_LONG.format(instant);
        }

    }

    public static String formatInstantSmartly(Temporal instant) {
        return formatInstantSmartly(Instant.now(), instant);
    }

}
