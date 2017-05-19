package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Getter
public class TimeLine {

    final Integer sequence;
    final Duration start;
    final Duration end;

    TimeLine(Integer sequence, Duration start, Duration end) {
        this.sequence = sequence;
        this.start = start;
        this.end = end;
    }
}
