package nl.vpro.domain.api;

import net.sf.json.test.JSONAssert;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.jackson2.Jackson2Mapper;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScheduleEventTest {

    @Test
    public void json() throws IOException {
        Program program = MediaTestDataBuilder.program().mid("VPROWON_12345").withScheduleEvents().creationDate(new Date(1409733642642L)).build();
        ApiScheduleEvent scheduleEvent = new ApiScheduleEvent(program.getScheduleEvents().first(), program);
        String json = Jackson2Mapper.getInstance().writeValueAsString(scheduleEvent);

        JSONAssert.assertJsonEquals(json, "{\"channel\":\"NED3\",\"start\":100,\"guideDay\":-90000000,\"duration\":200,\"midRef\":\"VPROWON_12345\",\"media\":{\"objectType\":\"program\",\"mid\":\"VPROWON_12345\",\"creationDate\":1409733642642,\"sortDate\":100,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"scheduleEvents\":[{\"guideDay\":-90000000,\"start\":100,\"duration\":200,\"poProgID\":\"VPROWON_12345\",\"channel\":\"NED3\",\"midRef\":\"VPROWON_12345\"},{\"guideDay\":169200000,\"start\":259200300,\"duration\":50,\"poProgID\":\"VPROWON_12345\",\"channel\":\"NED3\",\"net\":\"ZAPP\",\"midRef\":\"VPROWON_12345\"},{\"guideDay\":601200000,\"start\":691200350,\"duration\":250,\"poProgID\":\"VPROWON_12345\",\"channel\":\"HOLL\",\"midRef\":\"VPROWON_12345\"},{\"guideDay\":774000000,\"start\":864000600,\"duration\":200,\"poProgID\":\"VPROWON_12345\",\"channel\":\"CONS\",\"midRef\":\"VPROWON_12345\"}],\"workflow\":\"FOR_PUBLICATION\"}}", json);
    }

    @Test
    //@Ignore("Fails for https://java.net/jira/browse/JAXB-1069")
    public void xml() throws IOException, SAXException {
        Program program = MediaTestDataBuilder.program().mid("VPROWON_12345").withScheduleEvents().creationDate(new Date(1409733642642L)).build();
        ApiScheduleEvent scheduleEvent = new ApiScheduleEvent(program.getScheduleEvents().first(), program);
        StringWriter writer = new StringWriter();
        JAXB.marshal(scheduleEvent, writer);
        String xml = writer.toString();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:scheduleItem channel=\"NED3\" midRef=\"VPROWON_12345\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "    <media:start>1970-01-01T01:00:00.100+01:00</media:start>\n" +
            "    <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "    <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "    <media:program embeddable=\"true\" mid=\"VPROWON_12345\" sortDate=\"1970-01-01T01:00:00.100+01:00\" creationDate=\"2014-09-03T10:40:42.642+02:00\" workflow=\"FOR PUBLICATION\">\n" +
            "        <media:credits/>\n" +
            "        <media:locations/>\n" +
            "        <media:scheduleEvents>\n" +
            "            <media:scheduleEvent channel=\"NED3\" midRef=\"VPROWON_12345\">\n" +
            "                <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-01T01:00:00.100+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"NED3\" midRef=\"VPROWON_12345\" net=\"ZAPP\">\n" +
            "                <media:guideDay>1970-01-03+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-04T01:00:00.300+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.050S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"HOLL\" midRef=\"VPROWON_12345\">\n" +
            "                <media:guideDay>1970-01-08+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-09T01:00:00.350+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.250S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "            <media:scheduleEvent channel=\"CONS\" midRef=\"VPROWON_12345\">\n" +
            "                <media:guideDay>1970-01-10+01:00</media:guideDay>\n" +
            "                <media:start>1970-01-11T01:00:00.600+01:00</media:start>\n" +
            "                <media:duration>P0DT0H0M0.200S</media:duration>\n" +
            "                <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            </media:scheduleEvent>\n" +
            "        </media:scheduleEvents>\n" +
            "        <media:images/>\n" +
            "        <media:segments/>\n" +
            "    </media:program>\n" +
            "</api:scheduleItem>";
        Diff diff = XMLUnit.compareXML(expected, xml);
        if (!diff.identical()) {
            assertThat(xml).isEqualTo(expected);
        }
    }
}
