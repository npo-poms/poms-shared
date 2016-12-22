package nl.vpro.domain.api;

import nl.vpro.domain.media.Schedule;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class DateRangePresetTest {


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
        assertThat(DateRangePreset.BEFORE_LAST_YEAR.getBegin().getTime()).isEqualTo(-9223372036854775808L);
        assertThat(DateRangePreset.BEFORE_LAST_YEAR.getEnd().getTime()).isEqualTo(lastYear);
    }
    @Test
    public void lastYear() {
        long lastYear = ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS).minusYears(1).toEpochSecond() * 1000;
        long today = ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS).toEpochSecond() * 1000;
        assertThat(DateRangePreset.LAST_YEAR.getBegin().getTime()).isEqualTo(lastYear);
        assertThat(DateRangePreset.LAST_YEAR.getEnd().getTime()).isEqualTo(today);
    }


}
