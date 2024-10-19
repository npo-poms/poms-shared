package nl.vpro.domain.api;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.api.media.ScheduleResult;
import nl.vpro.domain.media.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScheduleEventTest implements ComparableTheory<ScheduleEvent> {

    @Test
    public void json() throws IOException {
        Program program = MediaTestDataBuilder
            .program()
            .mid("VPROWON_12345")
            .withScheduleEvents()
            .creationDate(Instant.ofEpochMilli(1409733642642L))
            .build();
        ApiScheduleEvent scheduleEvent = new ApiScheduleEvent(program.getScheduleEvents().first(), program);
        String json = Jackson2Mapper.getInstance().writeValueAsString(scheduleEvent);

        Jackson2TestUtil.assertThatJson(json)
            .isSimilarTo("""
{
  "titles" : [ {
    "value" : "Main ScheduleEvent Title",
    "owner" : "BROADCASTER",
    "type" : "MAIN"
  } ],
  "descriptions" : [ {
    "value" : "Main ScheduleEvent Description",
    "owner" : "BROADCASTER",
    "type" : "MAIN"
  } ],
  "channel" : "NED3",
  "start" : 100,
  "guideDay" : -90000000,
  "duration" : 200,
  "midRef" : "VPROWON_12345",
  "poProgID" : "VPROWON_12345",
  "textSubtitles" : "Teletekst ondertitels",
  "textPage" : "888",
  "primaryLifestyle" : {
    "value" : "Praktische Familiemensen"
  },
  "secondaryLifestyle" : {
    "value" : "Zorgzame Duizendpoten"
  },
  "media" : {
    "objectType" : "program",
    "mid" : "VPROWON_12345",
    "workflow" : "FOR_PUBLICATION",
    "sortDate" : 100,
    "creationDate" : 1409733642642,
    "embeddable" : true,
    "broadcasters" : [ ],
    "genres" : [ ],
    "countries" : [ ],
    "languages" : [ ],
    "scheduleEvents" : [ {
      "titles" : [ {
        "value" : "Main ScheduleEvent Title",
        "owner" : "BROADCASTER",
        "type" : "MAIN"
      } ],
      "descriptions" : [ {
        "value" : "Main ScheduleEvent Description",
        "owner" : "BROADCASTER",
        "type" : "MAIN"
      } ],
      "channel" : "NED3",
      "start" : 100,
      "guideDay" : -90000000,
      "duration" : 200,
      "midRef" : "VPROWON_12345",
      "poProgID" : "VPROWON_12345",
      "textSubtitles" : "Teletekst ondertitels",
      "textPage" : "888",
      "primaryLifestyle" : {
        "value" : "Praktische Familiemensen"
      },
      "secondaryLifestyle" : {
        "value" : "Zorgzame Duizendpoten"
      }
    }, {
      "channel" : "NED3",
      "start" : 259200300,
      "guideDay" : 169200000,
      "duration" : 50,
      "midRef" : "VPROWON_12345",
      "poProgID" : "VPROWON_12345",
      "repeat" : {
        "isRerun" : true
      },
      "net" : "ZAPP"
    }, {
      "channel" : "HOLL",
      "start" : 691200350,
      "guideDay" : 601200000,
      "duration" : 250,
      "midRef" : "VPROWON_12345",
      "poProgID" : "VPROWON_12345",
      "repeat" : {
        "isRerun" : true
      }
    }, {
      "channel" : "CONS",
      "start" : 864000600,
      "guideDay" : 774000000,
      "duration" : 200,
      "midRef" : "VPROWON_12345",
      "poProgID" : "VPROWON_12345",
      "repeat" : {
        "isRerun" : true
      }
    } ]
  }
}""");
    }

    @Test
    //@Ignore("Fails for https://java.net/jira/browse/JAXB-1069")
    public void xml() {
        Program program = MediaTestDataBuilder.program().mid("VPROWON_12345").withScheduleEvents().creationDate(Instant.ofEpochMilli(1409733642642L)).build();
        ApiScheduleEvent scheduleEvent = new ApiScheduleEvent(program.getScheduleEvents().first(), program);
        StringWriter writer = new StringWriter();
        JAXB.marshal(scheduleEvent, writer);
        String xml = writer.toString();
        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <api:scheduleItem channel="NED3" midRef="VPROWON_12345" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <media:title owner="BROADCASTER" type="MAIN">Main ScheduleEvent Title</media:title>
                <media:description owner="BROADCASTER" type="MAIN">Main ScheduleEvent Description</media:description>
                <media:textSubtitles>Teletekst ondertitels</media:textSubtitles>
                <media:textPage>888</media:textPage>
                <media:guideDay>1969-12-31+01:00</media:guideDay>
                <media:start>1970-01-01T01:00:00.100+01:00</media:start>
                <media:duration>P0DT0H0M0.200S</media:duration>
                <media:poProgID>VPROWON_12345</media:poProgID>
                <media:primaryLifestyle>Praktische Familiemensen</media:primaryLifestyle>
                <media:secondaryLifestyle>Zorgzame Duizendpoten</media:secondaryLifestyle>
                <media:program embeddable="true" mid="VPROWON_12345" sortDate="1970-01-01T01:00:00.100+01:00" workflow="FOR PUBLICATION" creationDate="2014-09-03T10:40:42.642+02:00">
                    <media:credits/>
                    <media:locations/>
                    <media:images/>
                    <media:scheduleEvents>
                        <media:scheduleEvent channel="NED3" midRef="VPROWON_12345">
                            <media:title owner="BROADCASTER" type="MAIN">Main ScheduleEvent Title</media:title>
                            <media:description owner="BROADCASTER" type="MAIN">Main ScheduleEvent Description</media:description>
                            <media:textSubtitles>Teletekst ondertitels</media:textSubtitles>
                            <media:textPage>888</media:textPage>
                            <media:guideDay>1969-12-31+01:00</media:guideDay>
                            <media:start>1970-01-01T01:00:00.100+01:00</media:start>
                            <media:duration>P0DT0H0M0.200S</media:duration>
                            <media:poProgID>VPROWON_12345</media:poProgID>
                            <media:primaryLifestyle>Praktische Familiemensen</media:primaryLifestyle>
                            <media:secondaryLifestyle>Zorgzame Duizendpoten</media:secondaryLifestyle>
                        </media:scheduleEvent>
                        <media:scheduleEvent channel="NED3" midRef="VPROWON_12345" net="ZAPP">
                            <media:repeat isRerun="true"></media:repeat>
                            <media:guideDay>1970-01-03+01:00</media:guideDay>
                            <media:start>1970-01-04T01:00:00.300+01:00</media:start>
                            <media:duration>P0DT0H0M0.050S</media:duration>
                            <media:poProgID>VPROWON_12345</media:poProgID>
                        </media:scheduleEvent>
                        <media:scheduleEvent channel="HOLL" midRef="VPROWON_12345">
                            <media:repeat isRerun="true"></media:repeat>
                            <media:guideDay>1970-01-08+01:00</media:guideDay>
                            <media:start>1970-01-09T01:00:00.350+01:00</media:start>
                            <media:duration>P0DT0H0M0.250S</media:duration>
                            <media:poProgID>VPROWON_12345</media:poProgID>
                        </media:scheduleEvent>
                        <media:scheduleEvent channel="CONS" midRef="VPROWON_12345">
                            <media:repeat isRerun="true"></media:repeat>
                            <media:guideDay>1970-01-10+01:00</media:guideDay>
                            <media:start>1970-01-11T01:00:00.600+01:00</media:start>
                            <media:duration>P0DT0H0M0.200S</media:duration>
                            <media:poProgID>VPROWON_12345</media:poProgID>
                        </media:scheduleEvent>
                    </media:scheduleEvents>
                    <media:segments/>
                </media:program>
            </api:scheduleItem>
            """;
        Diff diff = DiffBuilder.compare(expected).withTest(xml).build();
        if (diff.hasDifferences()) {
            assertThat(xml).isEqualTo(expected);
        }
    }

      Program program = MediaTestDataBuilder.program().creationDate(Instant.ofEpochMilli(100)).mid("VPROWON_12345").withSubtitles().build();


    @Test
    public void testListMedia() {
        ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new ScheduleEvent(Channel.NED3, Instant.ofEpochMilli(0), Duration.ofMillis(100));

        apiEvent = new ApiScheduleEvent(mediaEvent, program);
        List<ApiScheduleEvent> events = new ArrayList<>();
        events.add(apiEvent);
        ScheduleResult result = new ScheduleResult(new Result<>(events, 0L, 10, Result.Total.approximate(1L)));


        String expected = """
            <api:scheduleResult total="1"  totalQualifier="APPROXIMATE" offset="0" max="10" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:items>
                    <api:item xsi:type="api:scheduleEventApiType" channel="NED3" midRef="VPROWON_12345" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <media:guideDay>1969-12-31+01:00</media:guideDay>
                        <media:start>1970-01-01T01:00:00+01:00</media:start>
                        <media:duration>P0DT0H0M0.100S</media:duration>
                        <media:poProgID>VPROWON_12345</media:poProgID>
                        <media:program embeddable="true" hasSubtitles="true" mid="VPROWON_12345" sortDate="1970-01-01T01:00:00.100+01:00" creationDate="1970-01-01T01:00:00.100+01:00" workflow="FOR PUBLICATION">
                            <media:availableSubtitles language="nl" type="CAPTION"/>
                            <media:credits/>
                            <media:locations/>
                            <media:images/>
                            <media:scheduleEvents/>
                            <media:segments/>
                        </media:program>
                    </api:item>
                </api:items>
            </api:scheduleResult>""";
        ScheduleResult roundtripped = JAXBTestUtil.roundTripAndSimilar(result, expected);
        assertThat(roundtripped.getItems().get(0).getChannel()).isEqualTo(Channel.NED3);
    }

    @Test
    public void testGetMedia() {
        ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new ScheduleEvent(Channel.NED3, Instant.EPOCH, Duration.ofMillis(100));
        Program program = MediaTestDataBuilder.program().creationDate(Instant.ofEpochMilli(100)).mid("VPROWON_12346").build();

        apiEvent = new ApiScheduleEvent(mediaEvent, program);

        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <api:scheduleItem channel="NED3" midRef="VPROWON_12346" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <media:guideDay>1969-12-31+01:00</media:guideDay>
                <media:start>1970-01-01T01:00:00+01:00</media:start>
                <media:duration>P0DT0H0M0.100S</media:duration>
                <media:poProgID>VPROWON_12346</media:poProgID>
                <media:program embeddable="true" mid="VPROWON_12346" sortDate="1970-01-01T01:00:00.100+01:00" creationDate="1970-01-01T01:00:00.100+01:00" workflow="FOR PUBLICATION">
                    <media:credits/>
                    <media:locations/>
                    <media:images/>
                    <media:scheduleEvents/>
                    <media:segments/>
                </media:program>
            </api:scheduleItem>""";
        ApiScheduleEvent roundtripped = JAXBTestUtil.roundTripAndSimilar(apiEvent, expected);
        assertThat(roundtripped.getChannel()).isEqualTo(Channel.NED3);
    }


    Instant now = Instant.now();
    @Override
    public Arbitrary<Object> datapoints() {
        return Arbitraries.of(
            ScheduleEvent.builder().build(),
            ScheduleEvent.builder().start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.NED1).start(now.plus(Duration.ofMinutes(10))).build()
        ).map(s -> s == null ? null : new ApiScheduleEvent(s, program));
    }
}
