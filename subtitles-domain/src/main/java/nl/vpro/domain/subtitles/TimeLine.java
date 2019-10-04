package nl.vpro.domain.subtitles;

import lombok.*;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Getter
@EqualsAndHashCode
@ToString
public class TimeLine {

    final Integer sequence;
    final Duration start;
    final Duration end;

    @lombok.Builder
    TimeLine(Integer sequence, Duration start, Duration end) {
        this.sequence = sequence;
        this.start = start;
        this.end = end;
    }
}
