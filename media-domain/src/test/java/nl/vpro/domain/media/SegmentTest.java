package nl.vpro.domain.media;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class SegmentTest {


    @Test
    public void xml() {
        String xml = "<segment midRef=\"RBX_NTR_2648108\" type=\"SEGMENT\" urnRef=\"urn:vpro:media:program:83538010\" avType=\"AUDIO\" embeddable=\"true\" mid=\"RBX_NTR_4965178\" sortDate=\"2013-11-01T04:36:35.076+01:00\" creationDate=\"2016-11-01T04:36:35.076+01:00\" lastModified=\"2016-11-01T04:36:35.105+01:00\" publishDate=\"2016-11-01T04:42:23.506+01:00\" urn=\"urn:vpro:media:segment:83538015\" workflow=\"PUBLISHED\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <crid>crid://item.radiobox2/372578</crid>\n" +
            "    <broadcaster id=\"NTR\">NTR</broadcaster>\n" +
            "    <title owner=\"RADIOBOX\" type=\"MAIN\">Monteverdi - Orfeo</title>\n" +
            "    <description owner=\"RADIOBOX\" type=\"MAIN\">Opname 12 oktober 2016, Opera LausanneJose Maria Lo Monaco &amp; Fernando Guimaraes &amp; Anais Yvoz &amp; Delphine Galou &amp; Nicolas Courjal &amp; Anicio Zorzi Giustiniani &amp; Alessandro Giangrande &amp; Mathilde Opinel</description>\n" +
            "    <duration>P0DT0H3M0.000S</duration>\n" +
            "    <credits/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:group:13405607\" midRef=\"AUTO_NTROPERALIVE\" type=\"SERIES\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:program:83538010\" midRef=\"RBX_NTR_2648108\" type=\"BROADCAST\"/>\n" +
            "    <locations/>\n" +
            "    <images>\n" +
            "        <shared:image owner=\"RADIOBOX\" type=\"PICTURE\" highlighted=\"false\" creationDate=\"2016-11-01T04:36:34.659+01:00\" lastModified=\"2016-11-01T04:36:35.079+01:00\" urn=\"urn:vpro:media:image:83538017\" workflow=\"PUBLISHED\">\n" +
            "            <shared:title>Orfeo ed Euridice</shared:title>\n" +
            "            <shared:imageUri>urn:vpro:image:780595</shared:imageUri>\n" +
            "            <shared:height>568</shared:height>\n" +
            "            <shared:width>480</shared:width>\n" +
            "        </shared:image>\n" +
            "    </images>\n" +
            "    <segmentOf midRef=\"RBX_NTR_2648108\" urnRef=\"urn:vpro:media:program:83538010\" type=\"CLIP\"/>\n" +
            "    <start>P0DT0H0M0.000S</start>\n" +
            "</segment>";
        Segment segment = JAXBTestUtil.unmarshal(xml,  Segment.class);
        JAXBTestUtil.roundTripAndSimilarAndEquals(segment, xml);
    }

    @Test
    public void json() {
        Segment segment = MediaBuilder.segment()
            .start(Duration.ofMillis(100))
            .creationDate(LocalDateTime.of(2017, 5, 9, 14, 0))
            .build();
        Jackson2TestUtil.roundTripAndSimilar(segment, "{\n" +
            "  \"objectType\" : \"segment\",\n" +
            "  \"type\" : \"SEGMENT\",\n" +
            "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "  \"sortDate\" : 1494331200000,\n" +
            "  \"creationDate\" : 1494331200000,\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"start\" : 100,\n" +
            "  \"segmentOf\" : { }\n" +
            "}\n");
    }

    @Test
    public void copy() {
        Program program = MediaBuilder.program().build();
        Segment segment = MediaBuilder.segment().mainTitle("bla").duration(Duration.ofSeconds(123)).parent(program).build();

        Segment copy = Segment.copy(segment);
        assertThat(copy.getMainTitle()).isEqualTo("bla");
        assertThat(copy.getDuration().get()).isEqualTo(Duration.ofSeconds(123));

    }

    @Test
    public void sortDate() {
        Program program = MediaBuilder.program(ProgramType.BROADCAST)
            .mid("parentMid")
            .creationDate(LocalDateTime.of(2017, 10, 24, 0, 0))
            .build();
        Segment segment = MediaBuilder.segment()
            .mid("segmentMid")
            .creationDate(LocalDateTime.of(2017, 10, 25, 0, 0))
            .mainTitle("bla")
            .duration(Duration.ofSeconds(123))
            .parent(program)
            .build();

        assertThat(segment.getSortInstant()).isEqualTo(program.getCreationInstant());

        assertThat(
            JAXBTestUtil.roundTripAndSimilar(segment, "<segment midRef=\"parentMid\" type=\"SEGMENT\" embeddable=\"true\" mid=\"segmentMid\" sortDate=\"2017-10-24T00:00:00+02:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2017-10-25T00:00:00+02:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <title owner=\"BROADCASTER\" type=\"MAIN\">bla</title>\n" +
                "    <duration>P0DT0H2M3.000S</duration>\n" +
                "    <credits/>\n" +
                "    <descendantOf midRef=\"parentMid\" type=\"BROADCAST\"/>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <segmentOf midRef=\"parentMid\" type=\"BROADCAST\"/>\n" +
                "</segment>").getSortInstant()
        ).isEqualTo(program.getCreationInstant());

        assertThat(
            Jackson2TestUtil.roundTripAndSimilar(segment, "{\n" +
                "  \"objectType\" : \"segment\",\n" +
                "  \"mid\" : \"segmentMid\",\n" +
                "  \"type\" : \"SEGMENT\",\n" +
                "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
                "  \"sortDate\" : 1508796000000,\n" +
                "  \"creationDate\" : 1508882400000,\n" +
                "  \"embeddable\" : true,\n" +
                "  \"broadcasters\" : [ ],\n" +
                "  \"titles\" : [ {\n" +
                "    \"value\" : \"bla\",\n" +
                "    \"owner\" : \"BROADCASTER\",\n" +
                "    \"type\" : \"MAIN\"\n" +
                "  } ],\n" +
                "  \"genres\" : [ ],\n" +
                "  \"countries\" : [ ],\n" +
                "  \"languages\" : [ ],\n" +
                "  \"duration\" : 123000,\n" +
                "  \"descendantOf\" : [ {\n" +
                "    \"midRef\" : \"parentMid\",\n" +
                "    \"type\" : \"BROADCAST\"\n" +
                "  } ],\n" +
                "  \"midRef\" : \"parentMid\",\n" +
                "  \"segmentOf\" : {\n" +
                "    \"midRef\" : \"parentMid\",\n" +
                "    \"type\" : \"BROADCAST\"\n" +
                "    }\n" +
                "}\n").getSortInstant()
        ).isEqualTo(program.getCreationInstant());
    }

}
