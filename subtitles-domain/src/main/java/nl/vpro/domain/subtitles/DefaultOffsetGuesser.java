package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class DefaultOffsetGuesser implements Function<TimeLine, Duration> {


    public static DefaultOffsetGuesser INSTANCE = new DefaultOffsetGuesser();
    private DefaultOffsetGuesser() {

    }
    @Override
    public Duration apply(TimeLine timeline) {

        if (timeline.start.compareTo(Duration.ofMinutes(5)) < 0) {
            log.debug("This was probably not a live broadcast");
            return Duration.ofMinutes(2);
        } else {
            log.debug("This was probably a live broadcast. The first cues indicates a time of the day");
            return timeline.start.plus(Duration.ofSeconds(3));
        }

    }
}
