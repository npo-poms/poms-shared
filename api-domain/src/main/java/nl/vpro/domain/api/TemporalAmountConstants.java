package nl.vpro.domain.api;

import java.time.ZoneId;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class TemporalAmountConstants {

    private TemporalAmountConstants() {
    }

    public static final String TIMEZONE = "CET";

    public static final ZoneId ZONE = ZoneId.of(TIMEZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");
}


