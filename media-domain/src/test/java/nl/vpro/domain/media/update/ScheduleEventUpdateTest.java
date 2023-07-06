package nl.vpro.domain.media.update;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.media.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;

class ScheduleEventUpdateTest {

    /**
     * We explicitly annotated {@link Channel} with {@link nl.vpro.jackson2.BackwardsCompatibleJsonEnum}, causing that the XmlEnumValue is not used.
     * <p>
     * We could consider changing this, but for now it is like this.
     */
    @Test
    public void testJsonChannelXmlValue() {
        ScheduleEventUpdate e = ScheduleEventUpdate.builder()
            .localStart(of(2017, 8, 28, 15, 51))
            .guideDay(LocalDate.of(2017, 8, 28))
            .channel(Channel._10TB)
            .repeat(Repeat.rerun("herhaling"))
            .duration(Duration.ofMinutes(10))
            .title(TitleUpdate.main("test"))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(e, """
            {
                    "channel" : "_10TB",
                    "start" : 1503928260000,
                    "duration" : 600000,
                    "repeat" : {
                      "value" : "herhaling",
                      "isRerun" : true
                    },
                    "guideDay" : '2017-08-28',
                    "titles" : [ {
                      "value" : "test",
                      "type" : "MAIN"
                    } ]
                  }
            """);
    }


    /**
     */
    @Test
    public void parseIso() throws JsonProcessingException {
        ScheduleEventUpdate scheduleEventUpdate = Jackson2Mapper.getStrictInstance().readValue("""
            {
                  "channel" : "_10TB",
                  "start" : "2023-06-05T10:30:00+02:00",
                  "duration" : "P0DT0H30M0.000S",
                  "repeat" : {
                    "value" : "herhaling",
                    "isRerun" : true
                  },
                  "guideDay" : "2023-06-05"
                }
             """, ScheduleEventUpdate.class);
        assertThat(scheduleEventUpdate.getDuration()).isEqualTo(Duration.ofMinutes(30));
        assertThat(scheduleEventUpdate.getGuideDay()).isEqualTo(LocalDate.of(2023, 6, 5));
        assertThat(scheduleEventUpdate.getStart()).isEqualTo(of(2023, 6, 5, 10, 30).atZone(Schedule.ZONE_ID).toInstant());

    }

    /**
     */
    @Test
    public void parseMillis() throws JsonProcessingException {
        ScheduleEventUpdate scheduleEventUpdate = Jackson2Mapper.getStrictInstance().readValue("""
           {
                    "channel" : "_10TB",
                    "start" : 1503928260000,
                    "duration" : 600000,
                    "repeat" : {
                      "value" : "herhaling",
                      "isRerun" : true
                    },
                    "guideDay" : 1503871200000
           }
             """, ScheduleEventUpdate.class);
        assertThat(scheduleEventUpdate.getDuration()).isEqualTo(Duration.ofMinutes(10));
        assertThat(scheduleEventUpdate.getGuideDay()).isEqualTo(LocalDate.of(2017, 8, 28));
        assertThat(scheduleEventUpdate.getStart()).isEqualTo(of(2017, 8, 28, 15, 51).atZone(Schedule.ZONE_ID).toInstant());

    }

}
