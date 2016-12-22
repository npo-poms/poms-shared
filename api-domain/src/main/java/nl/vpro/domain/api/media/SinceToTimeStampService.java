package nl.vpro.domain.api.media;

import java.time.Instant;
import java.time.LocalDate;

import nl.vpro.domain.media.Schedule;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface SinceToTimeStampService {

    Instant DIVIDING_SINCE = LocalDate.of(2000, 1, 1).atStartOfDay(Schedule.ZONE_ID).toInstant();


    Instant getInstance(Long since);

    Long getSince(Instant since);

}
