package nl.vpro.domain.api;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScheduleEventTest {

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
            .isSimilarTo("{\n" +
                "  \"channel\" : \"NED3\",\n" +
                "  \"start\" : 100,\n" +
                "  \"guideDay\" : -90000000,\n" +
                "  \"duration\" : 200,\n" +
                "  \"midRef\" : \"VPROWON_12345\",\n" +
                "  \"poProgID\" : \"VPROWON_12345\",\n" +
                "  \"textSubtitles\" : \"Teletekst ondertitels\",\n" +
                "  \"textPage\" : \"888\",\n" +
                "  \"primaryLifestyle\" : {\n" +
                "    \"value\" : \"Praktische Familiemensen\"\n" +
                "  },\n" +
                "  \"secondaryLifestyle\" : {\n" +
                "    \"value\" : \"Zorgzame Duizendpoten\"\n" +
                "  },\n" +
                "  \"media\" : {\n" +
                "    \"objectType\" : \"program\",\n" +
                "    \"mid\" : \"VPROWON_12345\",\n" +
                "    \"workflow\" : \"FOR_PUBLICATION\",\n" +
                "    \"sortDate\" : 100,\n" +
                "    \"creationDate\" : 1409733642642,\n" +
                "    \"embeddable\" : true,\n" +
                "    \"broadcasters\" : [ ],\n" +
                "    \"genres\" : [ ],\n" +
                "    \"countries\" : [ ],\n" +
                "    \"languages\" : [ ],\n" +
                "    \"scheduleEvents\" : [ {\n" +
                "      \"titles\" : [ {\n" +
                "        \"value\" : \"Main ScheduleEvent Title\",\n" +
                "        \"owner\" : \"BROADCASTER\",\n" +
                "        \"type\" : \"MAIN\"\n" +
                "      } ],\n" +
                "      \"descriptions\" : [ {\n" +
                "        \"value\" : \"Main ScheduleEvent Description\",\n" +
                "        \"owner\" : \"BROADCASTER\",\n" +
                "        \"type\" : \"MAIN\"\n" +
                "      } ],\n" +
                "      \"channel\" : \"NED3\",\n" +
                "      \"start\" : 100,\n" +
                "      \"guideDay\" : -90000000,\n" +
                "      \"duration\" : 200,\n" +
                "      \"midRef\" : \"VPROWON_12345\",\n" +
                "      \"poProgID\" : \"VPROWON_12345\",\n" +
                "      \"textSubtitles\" : \"Teletekst ondertitels\",\n" +
                "      \"textPage\" : \"888\",\n" +
                "      \"primaryLifestyle\" : {\n" +
                "        \"value\" : \"Praktische Familiemensen\"\n" +
                "      },\n" +
                "      \"secondaryLifestyle\" : {\n" +
                "        \"value\" : \"Zorgzame Duizendpoten\"\n" +
                "      }\n" +
                "    }, {\n" +
                "      \"channel\" : \"NED3\",\n" +
                "      \"start\" : 259200300,\n" +
                "      \"guideDay\" : 169200000,\n" +
                "      \"duration\" : 50,\n" +
                "      \"midRef\" : \"VPROWON_12345\",\n" +
                "      \"poProgID\" : \"VPROWON_12345\",\n" +
                "      \"repeat\" : {\n" +
                "        \"isRerun\" : true\n" +
                "      },\n" +
                "      \"net\" : \"ZAPP\"\n" +
                "    }, {\n" +
                "      \"channel\" : \"HOLL\",\n" +
                "      \"start\" : 691200350,\n" +
                "      \"guideDay\" : 601200000,\n" +
                "      \"duration\" : 250,\n" +
                "      \"midRef\" : \"VPROWON_12345\",\n" +
                "      \"poProgID\" : \"VPROWON_12345\",\n" +
                "      \"repeat\" : {\n" +
                "        \"isRerun\" : true\n" +
                "      }\n" +
                "    }, {\n" +
                "      \"channel\" : \"CONS\",\n" +
                "      \"start\" : 864000600,\n" +
                "      \"guideDay\" : 774000000,\n" +
                "      \"duration\" : 200,\n" +
                "      \"midRef\" : \"VPROWON_12345\",\n" +
                "      \"poProgID\" : \"VPROWON_12345\",\n" +
                "      \"repeat\" : {\n" +
                "        \"isRerun\" : true\n" +
                "      }\n" +
                "    } ]\n" +
                "  }\n" +
                "}");
    }

    @Test
    //@Ignore("Fails for https://java.net/jira/browse/JAXB-1069")
    public void xml() {
        Program program = MediaTestDataBuilder.program().mid("VPROWON_12345").withScheduleEvents().creationDate(Instant.ofEpochMilli(1409733642642L)).build();
        ApiScheduleEvent scheduleEvent = new ApiScheduleEvent(program.getScheduleEvents().first(), program);
        StringWriter writer = new StringWriter();
        JAXB.marshal(scheduleEvent, writer);
        String xml = writer.toString();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:scheduleItem channel=\"NED3\" midRef=\"VPROWON_12345\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <media:textSubtitles>Teletekst ondertitels</media:textSubtitles>\n" +
            "    <media:textPage>888</media:textPage>\n" +
            "    <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "    <media:start>1970-01-01T01:00:00.100+01:00</media:start>\n" +
            "    <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "    <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "    <media:primaryLifestyle>Praktische Familiemensen</media:primaryLifestyle>\n" +
            "    <media:secondaryLifestyle>Zorgzame Duizendpoten</media:secondaryLifestyle>\n" +
            "    <media:program embeddable=\"true\" mid=\"VPROWON_12345\" sortDate=\"1970-01-01T01:00:00.100+01:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2014-09-03T10:40:42.642+02:00\">\n" +
            "        <media:credits/>\n" +
            "        <media:locations/>\n" +
            "        <media:images/>\n" +
            "        <media:scheduleEvents>\n" +
            "            <media:scheduleEvent channel=\"NED3\" midRef=\"VPROWON_12345\">\n" +
            "                <media:title owner=\"BROADCASTER\" type=\"MAIN\">Main ScheduleEvent Title</media:title>\n" +
            "                <media:description owner=\"BROADCASTER\" type=\"MAIN\">Main ScheduleEvent Description</media:description>\n" +
            "                <media:textSubtitles>Teletekst ondertitels</media:textSubtitles>\n" +
            "                <media:textPage>888</media:textPage>\n" +
            "                <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-01T01:00:00.100+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "                <media:primaryLifestyle>Praktische Familiemensen</media:primaryLifestyle>\n" +
            "                <media:secondaryLifestyle>Zorgzame Duizendpoten</media:secondaryLifestyle>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"NED3\" midRef=\"VPROWON_12345\" net=\"ZAPP\">\n" +
            "                <media:repeat isRerun=\"true\"></media:repeat>\n" +
            "                <media:guideDay>1970-01-03+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-04T01:00:00.300+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.050S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"HOLL\" midRef=\"VPROWON_12345\">\n" +
            "                <media:repeat isRerun=\"true\"></media:repeat>\n" +
            "                <media:guideDay>1970-01-08+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-09T01:00:00.350+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.250S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"CONS\" midRef=\"VPROWON_12345\">\n" +
            "                <media:repeat isRerun=\"true\"></media:repeat>\n" +
            "                <media:guideDay>1970-01-10+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-11T01:00:00.600+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "        </media:scheduleEvents>\n" +
            "        <media:segments/>\n" +
            "    </media:program>\n" +
            "</api:scheduleItem>\n";
        Diff diff = DiffBuilder.compare(expected).withTest(xml).build();
        if (diff.hasDifferences()) {
            assertThat(xml).isEqualTo(expected);
        }
    }
}
