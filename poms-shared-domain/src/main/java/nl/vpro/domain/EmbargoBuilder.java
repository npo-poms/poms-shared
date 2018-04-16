package nl.vpro.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import nl.vpro.util.DateUtils;

import static nl.vpro.util.DateUtils.toDate;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface EmbargoBuilder<B extends EmbargoBuilder<B>> {

    ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");


    default B publishStart(Date date) {
        return publishStart(DateUtils.toInstant(date));
    }

    B publishStart(Instant date);

    default B publishStart(ZonedDateTime date) {
        return publishStart(toDate(date));
    }

    default B publishStart(LocalDateTime date) {
        return publishStart(fromLocalDate(date));
    }

    default B publishStop(Date date) {
        return publishStop(DateUtils.toInstant(date));
    }

    B publishStop(Instant date);

    default B publishStop(LocalDateTime date) {
        return publishStop(fromLocalDate(date));
    }

    static Date fromLocalDate(LocalDateTime date) {
        return DateUtils.toDate(date, ZONE_ID);

    }
}
