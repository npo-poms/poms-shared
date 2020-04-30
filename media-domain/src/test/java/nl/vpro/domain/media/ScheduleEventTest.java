package nl.vpro.domain.media;

import java.time.Duration;

import org.junit.Test;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class ScheduleEventTest {

    @Test
    public void testTitles() {

        ScheduleEvent e = new ScheduleEvent();
        e.setTitle("bbb", TextualType.ABBREVIATION);
        e.setMainTitle("aa");

        assertThat(e.getTitles().first().get()).isEqualTo("aa");

        JAXBTestUtil.roundTripAndSimilar(e, "<local:scheduleEvent xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <title owner=\"BROADCASTER\" type=\"MAIN\">aa</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"ABBREVIATION\">bbb</title>\n" +
            "</local:scheduleEvent>");
    }

    @Test
    public void testJson() {
        ScheduleEvent e = ScheduleEvent.builder()
            .localStart(of(2017, 8, 28, 15, 51))
            .channel(Channel.NED1)
            .duration(Duration.ofMinutes(10))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(e, "{\n" +
            "  \"channel\" : \"NED1\",\n" +
            "  \"start\" : 1503928260000,\n" +
            "  \"guideDay\" : 1503871200000\n," +
            "  \"duration\" : 600000\n" +
            "}");
    }

    @Test
    public void testJsonPublisher() {
        ScheduleEvent e = ScheduleEvent.builder()
            .localStart(of(2017, 8, 28, 15, 51))
            .channel(Channel.NED1)
            .duration(Duration.ofMinutes(10))
            .mainTitle("scheduleEventTitle")
            .mainDescription("scheduleEventDescription")
            .primaryLifestyle(new Lifestyle("primary lifestyle"))
            .secondaryLifestyle(new SecondaryLifestyle("secondary lifestyle"))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.PRETTY_PUBLISHER, e, "\"{\n" +
            "  \"titles\" : [ {\n" +
            "    \"value\" : \"scheduleEventTitle\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  } ],\n" +
            "  \"descriptions\" : [ {\n" +
            "    \"value\" : \"scheduleEventDescription\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  } ],\n" +
            "  \"channel\" : \"NED1\",\n" +
            "  \"start\" : 1503928260000,\n" +
            "  \"guideDay\" : 1503871200000,\n" +
            "  \"duration\" : 600000,\n" +
            "  \"primaryLifestyle\" : {\n" +
            "    \"value\" : \"primary lifestyle\"\n" +
            "  },\n" +
            "  \"secondaryLifestyle\" : {\n" +
            "    \"value\" : \"secondary lifestyle\"\n" +
            "  },\n" +
            "  \"rerun\" : false,\n" +
            "  \"eventStart\" : 1503928260000\n" +
            "}");
    }

}
