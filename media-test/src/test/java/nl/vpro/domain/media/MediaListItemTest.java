package nl.vpro.domain.media;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class MediaListItemTest {

    @Test
    public void xml() throws IOException, SAXException {
        Program program = MediaTestDataBuilder.program().withEverything().build();
        MediaListItem item = new MediaListItem(program);

        JAXBTestUtil.roundTripAndSimilar(item, "<s:item mid=\"VPROWON_20001\" avType=\"VIDEO\" mediaType=\"nl.vpro.domain.media.Program\" id=\"12\" urn=\"urn:vpro:media:program:12\" workflow=\"PUBLISHED\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <s:broadcaster id=\"BNN\">BNN</s:broadcaster>\n" +
            "    <s:broadcaster id=\"AVRO\">AVRO</s:broadcaster>\n" +
            "    <s:title>Main title</s:title>\n" +
            "    <s:subTitle>Episode title MIS</s:subTitle>\n" +
            "    <s:description>Main description</s:description>\n" +
            "    <s:creationDate>2015-03-06T00:00:00+01:00</s:creationDate>\n" +
            "    <s:lastModified>2015-03-06T01:00:00+01:00</s:lastModified>\n" +
            "    <s:createdBy>editor@vpro.nl</s:createdBy>\n" +
            "    <s:lastModifiedBy>editor@vpro.nl</s:lastModifiedBy>\n" +
            "    <s:sortDate>1970-01-01T01:00:00.100+01:00</s:sortDate>\n" +
            "    <s:type>BROADCAST</s:type>\n" +
            "    <s:publishStart>1970-01-01T01:00:00+01:00</s:publishStart>\n" +
            "    <s:publishStop>2500-01-01T00:00:00+01:00</s:publishStop>\n" +
            "    <s:lastPublished>2015-03-06T02:00:00+01:00</s:lastPublished>\n" +
            "    <s:firstScheduleEvent channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "        <poProgID>VPROWON_20001</poProgID>\n" +
            "    </s:firstScheduleEvent>\n" +
            "    <s:firstScheduleEventNoRerun channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "        <poProgID>VPROWON_20001</poProgID>\n" +
            "    </s:firstScheduleEventNoRerun>\n" +
            "    <s:lastScheduleEvent channel=\"CONS\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "        <repeat isRerun=\"true\"></repeat>\n" +
            "        <guideDay>1970-01-10+01:00</guideDay>\n" +
            "        <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "        <poProgID>VPROWON_20001</poProgID>\n" +
            "    </s:lastScheduleEvent>\n" +
            "    <s:lastScheduleEventNoRerun channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "        <poProgID>VPROWON_20001</poProgID>\n" +
            "    </s:lastScheduleEventNoRerun>\n" +
            "    <s:sortDateScheduleEvent channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "        <poProgID>VPROWON_20001</poProgID>\n" +
            "    </s:sortDateScheduleEvent>\n" +
            "    <s:locations owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T15:45:00+01:00\" urn=\"urn:vpro:media:location:6\">\n" +
            "        <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>MP4</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "        <offset>P0DT0H13M0.000S</offset>\n" +
            "        <duration>P0DT0H10M0.000S</duration>\n" +
            "    </s:locations>\n" +
            "    <s:locations owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T14:45:00+01:00\" urn=\"urn:vpro:media:location:7\">\n" +
            "        <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>WM</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "    </s:locations>\n" +
            "    <s:locations owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T13:45:00+01:00\" urn=\"urn:vpro:media:location:8\">\n" +
            "        <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>WM</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "        <duration>P0DT0H30M33.000S</duration>\n" +
            "    </s:locations>\n" +
            "    <s:locations owner=\"NEBO\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T12:45:00+01:00\" urn=\"urn:vpro:media:location:9\">\n" +
            "        <programUrl>http://player.omroep.nl/?aflID=4393288</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>HTML</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "    </s:locations>\n" +
            "    <s:locations owner=\"NPO\" workflow=\"FOR PUBLICATION\" creationDate=\"2017-03-04T15:45:00+01:00\" urn=\"urn:vpro:media:location:10\">\n" +
            "        <programUrl>http://www.npo.nl/location/1</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>UNKNOWN</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "        <offset>P0DT0H13M0.000S</offset>\n" +
            "        <duration>P0DT0H10M0.000S</duration>\n" +
            "    </s:locations>\n" +
            "    <s:locations owner=\"BROADCASTER\" workflow=\"PUBLISHED\" creationDate=\"2017-02-05T11:42:00+01:00\" urn=\"urn:vpro:media:location:11\">\n" +
            "        <programUrl>http://www.vpro.nl/location/1</programUrl>\n" +
            "        <avAttributes>\n" +
            "            <avFileFormat>UNKNOWN</avFileFormat>\n" +
            "        </avAttributes>\n" +
            "    </s:locations>\n" +
            "    <s:numberOfLocations>6</s:numberOfLocations>\n" +
            "    <s:tag>tag1</s:tag>\n" +
            "    <s:tag>tag2</s:tag>\n" +
            "    <s:tag>tag3</s:tag>\n" +
            "</s:item>");
    }
}
