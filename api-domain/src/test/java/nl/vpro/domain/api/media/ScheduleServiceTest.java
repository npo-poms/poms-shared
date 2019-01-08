package nl.vpro.domain.api.media;

import java.time.LocalDate;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleServiceTest {

    @Test
    public void testGuideDayStart() {
        assertThat(ScheduleService.guideDayStart(LocalDate.parse("2015-06-07")).toString()).isEqualTo("2015-06-07T06:00+02:00[Europe/Amsterdam]");
    }

    @Test
    public void testGuideDayStop() {
        assertThat(ScheduleService.guideDayStop(LocalDate.parse("2015-06-07")).toString()).isEqualTo("2015-06-08T06:00+02:00[Europe/Amsterdam]");
    }

    @Test
    public void testGuideDayStartSummertime() {
        assertThat(ScheduleService.guideDayStart(LocalDate.parse("2015-03-28")).toString()).isEqualTo("2015-03-28T06:00+01:00[Europe/Amsterdam]");
    }

    @Test
    public void testGuideDayStopSummertime() {
        assertThat(ScheduleService.guideDayStop(LocalDate.parse("2015-03-28")).toString()).isEqualTo("2015-03-29T06:00+02:00[Europe/Amsterdam]");
    }
}
