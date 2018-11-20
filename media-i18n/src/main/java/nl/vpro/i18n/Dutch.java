package nl.vpro.i18n;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * @author Michiel Meeuwissen
 * @since 5.9
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


    /**
     * Like {@link #formatSmartly(Temporal)}, but with the option to specify 'now'. Mainly usefull for testing.
     */
    public static String formatSmartly(Temporal now, Temporal instant) {

        Duration distance = Duration.between(now, instant).abs();
        if (distance.compareTo(Duration.ofHours(12)) < 0) {
            return FORMATTER_SHORT.format(instant);
        } else if (distance.compareTo(Duration.ofDays(365).dividedBy(2)) < 0) {
            return FORMATTER_LONG.format(instant);
        } else {
            return FORMATTER_VERY_LONG.format(instant);
        }
    }

    /**
     * Formats a date time smartly, I.e. it will use shorter representation if this temporal is closer to {@link Instant#now()}.
     *<ul>
     * <li>If it's closer than 12 hour, then only a time will be displayed,</li>
     * <li>If it's closer than half a year, then a date and a time will be displayed (but without an year)</li>
     * <li>Otherwise a full date time string will be returned.</li>
     * </ul>
     */
    public static String formatSmartly(Temporal instant) {
        return formatSmartly(Instant.now(), instant);
    }

}
