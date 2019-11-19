package nl.vpro.domain.subtitles;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class TT888Test {
    @Test
    public void parseTimeline() {
        String line = "0003 00:02:08:11 00:02:11:06";
        TimeLine timeLine = TT888.parseTimeline(line);
        assertThat(timeLine.getSequence()).isEqualTo(3);
        assertThat(timeLine.getStart()).isEqualTo(Duration.ofMillis((2 * 60 + 8) * 1000L + 110));
        assertThat(timeLine.getEnd()).isEqualTo(Duration.ofMillis((2 * 60 + 11) * 1000L + 60));
    }
}
