package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Function;

/**
 * See NPA-390
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class DefaultOffsetGuesser implements Function<TimeLine, Duration> {

    static final Instant AFTER_THIS_DATE_ALSO_NON_LIVE_BROADCASTS_HAVE_NULL_OFFSET = LocalDate.of(2017, 4, 1).atStartOfDay().atZone(ZoneId.of("Europe/Amsterdam")).toInstant();

    static final Duration FIRST_CUE_OF_LIVE = Duration.ofSeconds(3); // The assumption is that the first cue of a live broadcast always is at 3 seconds

    static final Duration OFFSET_OF_RECORDED = Duration.ofMinutes(2); // For recorded broadcasts, the subtitles always have this offset

    static final Duration FIRST_CUE_RECORDED_MIN = Duration.ofMinutes(1);
    static final Duration FIRST_CUE_RECORDED_MAX= Duration.ofMinutes(5);

    final Instant creationDate;
    public DefaultOffsetGuesser(Instant creationDate) {
        this.creationDate = creationDate;
    }
    @Override
    public Duration apply(TimeLine timeline) {
        if (timeline.end.minus(timeline.start).compareTo(Duration.ofMinutes(30)) > 0)  {
            log.debug("This must be live. We sometimes saw very long initial queues. We suppose that the offset is end");
            return timeline.end.minus(FIRST_CUE_OF_LIVE);
        }
        if (creationDate.isAfter(AFTER_THIS_DATE_ALSO_NON_LIVE_BROADCASTS_HAVE_NULL_OFFSET) &&
            timeline.start.compareTo(FIRST_CUE_RECORDED_MAX) < 0 &&
            timeline.start.compareTo(FIRST_CUE_RECORDED_MIN) > 0) {
            log.debug("This was probably not a live broadcast");
            return OFFSET_OF_RECORDED;
        } else {
            log.debug("This was probably a live broadcast. Cues are indicated by time of time. The first cue defines the offset for all of them");
            return timeline.start.minus(FIRST_CUE_OF_LIVE);
        }

    }
}
