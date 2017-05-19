package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Function;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class DefaultOffsetGuesser implements Function<TimeLine, Duration> {

    static final Instant AFTER_THIS_DATE_ALSO_NON_LIVE_BROADCASTS_HAVE_NULL_OFFSET = LocalDate.of(2017, 4, 1).atStartOfDay().atZone(ZoneId.of("Europe/Amsterdam")).toInstant();

    final Instant creationDate;
    public DefaultOffsetGuesser(Instant creationDate) {
        this.creationDate = creationDate;
    }
    @Override
    public Duration apply(TimeLine timeline) {
        if (creationDate.isAfter(AFTER_THIS_DATE_ALSO_NON_LIVE_BROADCASTS_HAVE_NULL_OFFSET) && timeline.start.compareTo(Duration.ofMinutes(5)) < 0) {
            log.debug("This was probably not a live broadcast");
            return Duration.ofMinutes(2);
        } else {
            log.debug("This was probably a live broadcast. Cues are indicated by time of time. The first cue defines the offset for all of them");
            return timeline.start.plus(Duration.ofSeconds(3));
        }

    }
}
