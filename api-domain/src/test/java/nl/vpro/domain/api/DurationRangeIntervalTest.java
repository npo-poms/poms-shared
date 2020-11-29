package nl.vpro.domain.api;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class DurationRangeIntervalTest {


    @Test
    public void matches1Minute() {
        assertThat(new DurationRangeInterval("1 minute").getInterval().getDuration()).isEqualTo(Duration.ofMinutes(1));
        assertThat(new DurationRangeInterval("1 minute").matches(Duration.ofMinutes(0), Duration.ofMinutes(1))).isTrue();
        assertThat(new DurationRangeInterval("1 minute").matches(Duration.ofSeconds(1), Duration.ofMinutes(1))).isFalse();
        assertThat(new DurationRangeInterval("1 minute").matches(Duration.ofMinutes(1), Duration.ofMinutes(2))).isTrue();
        assertThat(new DurationRangeInterval("1 minute").matches(Duration.ofMinutes(2), Duration.ofMinutes(4))).isFalse();
        assertThat(new DurationRangeInterval("1 minute").getInterval().getDuration()).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    public void matches2Minutes() {
        assertThat(new DurationRangeInterval("2 minutes").getInterval().getDuration()).isEqualTo(Duration.ofMinutes(2));
        assertThat(new DurationRangeInterval("2 minutes").matches(Duration.ofMinutes(0), Duration.ofMinutes(2))).isTrue();
        assertThat(new DurationRangeInterval("2 minutes").matches(Duration.ofMinutes(1), Duration.ofMinutes(3))).isFalse();

    }


}
