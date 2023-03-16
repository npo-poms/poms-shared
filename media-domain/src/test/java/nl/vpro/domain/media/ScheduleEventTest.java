package nl.vpro.domain.media;

import net.jqwik.api.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.meeuw.util.test.ComparableTheory;

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
public class ScheduleEventTest implements ComparableTheory<ScheduleEvent> {

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
    public void testXmlDuration() {

        ScheduleEvent e = new ScheduleEvent();
        e.setChannel(Channel._10TB);
        e.setDuration(Duration.ofMinutes(10));

        JAXBTestUtil.roundTripAndSimilar(e, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><local:scheduleEvent xmlns:local=\"uri:local\" channel=\"10TB\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "  <duration>P0DT0H10M0.000S</duration>\n" +
            "</local:scheduleEvent>");
    }

    @Test
    public void testXmlDurationVeryLong() {

        ScheduleEvent e = new ScheduleEvent();
        e.setDuration(Duration.ofDays(800));

        JAXBTestUtil.roundTripAndSimilar(e, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><local:scheduleEvent xmlns:local=\"uri:local\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "  <duration>P2Y2M10DT0H0M0.000S</duration>\n" +
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


    /**
     * We explicitly annotated {@link Channel} with {@link nl.vpro.jackson2.BackwardsCompatibleJsonEnum}, causing that the XmlEnumValue is not used.
     * <p>
     * We could consider changing this, bug for now it is like this.
     */
    @Test
    public void testJsonChannelXmlValue() {
        ScheduleEvent e = ScheduleEvent.builder()
            .localStart(of(2017, 8, 28, 15, 51))
            .channel(Channel._10TB)
            .duration(Duration.ofMinutes(10))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(e, "{\n" +
            "  \"channel\" : \"_10TB\",\n" +
            "  \"start\" : 1503928260000,\n" +
            "  \"guideDay\" : 1503871200000\n," +
            "  \"duration\" : 600000\n" +
            "}");
    }

    @Test
    public void testJsonVeryLong() {
        ScheduleEvent e = ScheduleEvent.builder()
            .localStart(of(2017, 8, 28, 15, 51))
            .channel(Channel.NED1)
            .duration(Duration.ofDays(800))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(e, "{\n" +
            "  \"channel\" : \"NED1\",\n" +
            "  \"start\" : 1503928260000,\n" +
            "  \"guideDay\" : 1503871200000\n," +
            "  \"duration\" : 69120000000\n" +
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

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPrettyPublisherInstance(), e, "{\n" +
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



    static final Instant now = Instant.now();



    @Test
    public void testGuideDayBeforeCutOff() {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, Instant.EPOCH, Duration.ofMillis(10));

        assertThat(target.getGuideDate().toString()).isEqualTo("1969-12-31");
    }


    @Test
    public void testGuideDayAfterCutOff() {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli((5 * 3600 + 58 * 60) * 1000), Duration.ofMillis(10));

        assertThat(target.getGuideDate().toString()).isEqualTo("1970-01-01");
    }
    @Override
    public Arbitrary<? extends ScheduleEvent> datapoints() {
        return Arbitraries.of(
            ScheduleEvent.builder().build(),
            ScheduleEvent.builder().start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.NED1).start(now.plus(Duration.ofMinutes(10))).build()
        );
    }

    @Override
    public Arbitrary<? extends Tuple.Tuple2<? extends ScheduleEvent, ? extends ScheduleEvent>> equalDatapoints() {
        return Arbitraries.of(
            Tuple.of(ScheduleEvent.builder().build(), ScheduleEvent.builder().build()),
            Tuple.of(
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).build(),
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).build()),
             Tuple.of(
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).build(),
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).duration(Duration.ofMinutes(1)).build()),
            Tuple.of(
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).duration(Duration.ofMinutes(10)).build(),
                ScheduleEvent.builder().channel(Channel.ARTE).start(now).duration(Duration.ofMinutes(1)).build())

        );
     }
}
