package nl.vpro.domain.api;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class InstantRangePresetTest {


    @Test
    public void all() {
        StringBuilder builder = new StringBuilder();
        for (DateRangePreset preset : DateRangePreset.values()) {
            builder.append(preset.name()).append(":").append(preset.getBegin()).append(":").append(preset.getEnd()).append("\n");
        }
        System.out.println(builder.toString());
    }

    @Test
    public void beforeLastYear() {
        long lastYear = ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS).minusYears(1).toEpochSecond() * 1000;
        assertThat(DateRangePreset.BEFORE_LAST_YEAR.getBegin().toEpochMilli()).isEqualTo(-9223372036854775808L);
        assertThat(DateRangePreset.BEFORE_LAST_YEAR.getEnd().toEpochMilli()).isEqualTo(lastYear);
    }
    @Test
    public void lastYear() {
        long lastYear = ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS).minusYears(1).toEpochSecond() * 1000;
        long today = ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS).toEpochSecond() * 1000;
        assertThat(DateRangePreset.LAST_YEAR.getBegin().toEpochMilli()).isEqualTo(lastYear);
        assertThat(DateRangePreset.LAST_YEAR.getEnd().toEpochMilli()).isEqualTo(today);
    }


}
