package nl.vpro.domain.media;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

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
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?><segment xmlns="urn:vpro:media:2009" midRef="RBX_NTR_2648108" type="SEGMENT" urnRef="urn:vpro:media:program:83538010" avType="AUDIO" embeddable="true" mid="RBX_NTR_4965178" sortDate="2013-11-01T04:36:35.076+01:00" workflow="PUBLISHED" creationDate="2016-11-01T04:36:35.076+01:00" lastModified="2016-11-01T04:36:35.105+01:00" publishDate="2016-11-01T04:42:23.506+01:00" urn="urn:vpro:media:segment:83538015" xmlns:shared="urn:vpro:shared:2009">
                <crid>crid://item.radiobox2/372578</crid>
                <broadcaster id="NTR">NTR</broadcaster>
                <title owner="RADIOBOX" type="MAIN">Monteverdi - Orfeo</title>
                <description owner="RADIOBOX" type="MAIN">Opname 12 oktober 2016, Opera LausanneJose Maria Lo Monaco &amp; Fernando Guimaraes &amp; Anais Yvoz &amp; Delphine Galou &amp; Nicolas Courjal &amp; Anicio Zorzi Giustiniani &amp; Alessandro Giangrande &amp; Mathilde Opinel</description>
                <duration>P0DT0H3M0.000S</duration>
                <credits/>
                <descendantOf urnRef="urn:vpro:media:group:13405607" midRef="AUTO_NTROPERALIVE" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:program:83538010" midRef="RBX_NTR_2648108" type="BROADCAST"/>
                <locations/>
                <images>
                    <shared:image owner="RADIOBOX" type="PICTURE" highlighted="false" workflow="PUBLISHED" creationDate="2016-11-01T04:36:34.659+01:00" lastModified="2016-11-01T04:36:35.079+01:00" urn="urn:vpro:media:image:83538017">
                        <shared:title>Orfeo ed Euridice</shared:title>
                        <shared:imageUri>urn:vpro:image:780595</shared:imageUri>
                        <shared:height>568</shared:height>
                        <shared:width>480</shared:width>
                    </shared:image>
                </images>
                <segmentOf midRef="RBX_NTR_2648108" type="CLIP"/>
                <start>P0DT0H0M0.000S</start>
            </segment>""";
        Segment segment = JAXBTestUtil.unmarshal(xml,  Segment.class);
        assertThat(segment.getSegmentOf().getParentMid()).isEqualTo("RBX_NTR_2648108");
        assertThat(segment.getSegmentOf().getType()).isEqualTo(MediaType.CLIP);
        JAXBTestUtil.roundTripAndSimilarAndEquals(segment, xml);

    }

    @Test
    public void json() {
        Segment segment = MediaBuilder.segment()
            .start(Duration.ofMillis(100))
            .segmentOf("bla", MediaType.CLIP)
            .creationDate(LocalDateTime.of(2017, 5, 9, 14, 0))

            .build();
        Jackson2TestUtil.roundTripAndSimilar(segment, """
            {
              "objectType" : "segment",
              "type" : "SEGMENT",
              "workflow" : "FOR_PUBLICATION",
              "sortDate" : 1494331200000,
              "creationDate" : 1494331200000,
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "start" : 100,
              "midRef" : "bla",
              "segmentOf" : {
                "midRef" : "bla",
                "type" : "CLIP"
              }
            }""");
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
            JAXBTestUtil.roundTripAndSimilar(segment, """
                <segment midRef="parentMid" type="SEGMENT" embeddable="true" mid="segmentMid" sortDate="2017-10-24T00:00:00+02:00" workflow="FOR PUBLICATION" creationDate="2017-10-25T00:00:00+02:00" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                    <title owner="BROADCASTER" type="MAIN">bla</title>
                    <duration>P0DT0H2M3.000S</duration>
                    <credits/>
                    <descendantOf midRef="parentMid" type="BROADCAST"/>
                    <locations/>
                    <images/>
                    <segmentOf midRef="parentMid" type="BROADCAST"/>
                </segment>""").getSortInstant()
        ).isEqualTo(program.getCreationInstant());

        assertThat(
            Jackson2TestUtil.roundTripAndSimilar(segment, """
                {
                  "objectType" : "segment",
                  "mid" : "segmentMid",
                  "type" : "SEGMENT",
                  "workflow" : "FOR_PUBLICATION",
                  "sortDate" : 1508796000000,
                  "creationDate" : 1508882400000,
                  "embeddable" : true,
                  "broadcasters" : [ ],
                  "titles" : [ {
                    "value" : "bla",
                    "owner" : "BROADCASTER",
                    "type" : "MAIN"
                  } ],
                  "genres" : [ ],
                  "countries" : [ ],
                  "languages" : [ ],
                  "duration" : 123000,
                  "descendantOf" : [ {
                    "midRef" : "parentMid",
                    "type" : "BROADCAST"
                  } ],
                  "midRef" : "parentMid",
                  "segmentOf" : {
                    "midRef" : "parentMid",
                    "type" : "BROADCAST"
                    }
                }
                """).getSortInstant()
        ).isEqualTo(program.getCreationInstant());
    }

}
