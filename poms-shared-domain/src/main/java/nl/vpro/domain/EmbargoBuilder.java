package nl.vpro.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.Range;

import nl.vpro.util.DateUtils;

import static nl.vpro.util.DateUtils.toDate;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface EmbargoBuilder<B extends EmbargoBuilder<B>> {

    ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");


    B  self();

    @Deprecated
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

    @Deprecated
    default B publishStop(Date date) {
        return publishStop(DateUtils.toInstant(date));
    }

    B publishStop(Instant date);

    default B publishStop(LocalDateTime date) {
        return publishStop(fromLocalDate(date));
    }

    default B range(@Nullable Range<Instant> range) {
        if (range != null) {
            if (range.hasLowerBound()) {
                publishStart(range.lowerEndpoint());
            }
            if (range.hasUpperBound()) {
                publishStop(range.upperEndpoint());
            }
        }
        return self();
    }
    static Instant fromLocalDate(LocalDateTime date) {
        return DateUtils.toInstant(date, ZONE_ID);

    }
}
