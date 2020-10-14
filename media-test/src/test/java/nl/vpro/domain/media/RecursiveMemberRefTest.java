package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@Slf4j
class RecursiveMemberRefTest {

    Group g1 = MediaTestDataBuilder.series().mid("g1").id(100L).withFixedDates().build();

    Group g2 = MediaTestDataBuilder.season().mid("g2").id(101L).withFixedDates().build();




    Program m1 = MediaTestDataBuilder.program().mid("m1").id(1L).withFixedDates().build();
    Program m2 = MediaTestDataBuilder.broadcast().mid("m2").id(2L).withFixedDates()
        .memberOf(m1)
        .episodeOf(g1, 1)
        .build();
    Program m3 = MediaTestDataBuilder.program().mid("m3").id(3L).withFixedDates()
        .memberOf(m2)
        .memberOf(m1)
        .build();


    Program m4 = MediaTestDataBuilder.broadcast().mid("m3").id(3L).withFixedDates()
        .episodeOf(g2, 1)
        .build();

    Segment s1 = MediaTestDataBuilder.segment(m4).mid("s1").id(4L).withFixedDates().build();

    {
        // force circular to  reproduce  MSE-4895
        m1.getMemberOf().add(new MemberRef(m1, m3, 1, OwnerType.BROADCASTER));
        g1.getMemberOf().add(new MemberRef(g1, m3, 2, OwnerType.BROADCASTER));

        // circular via segment of
        g2.getMemberOf().add(new MemberRef(g2, s1, 1, OwnerType.BROADCASTER));

    }


    @Test
    public void circular() {
        SortedSet<MemberRef> memberOf = m3.getMemberOf();
        MemberRef first = memberOf.first();
        log.info("{}", first.getMemberOf());
        assertThat(first.getMemberOf().first().getMemberOf().first().isCircular()).isTrue();
    }


    @Test
    public void circularViaSegment() {
        SortedSet<MemberRef> episodeOf = m4.getEpisodeOf();
        MemberRef first = episodeOf.first();
        log.info("{}", first.getMemberOf());
        assertThat(first.getMemberOf().first().getSegmentOf().getEpisodeOf().first().isCircular()).isTrue();
    }

    @Test
    public void marshalm3() {

        JAXBTestUtil.roundTripAndSimilar(m3,
            "<program xmlns=\"urn:vpro:media:2009\" embeddable=\"true\" mid=\"m3\" sortDate=\"2015-03-06T00:00:00+01:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2015-03-06T00:00:00+01:00\" lastModified=\"2015-03-06T01:00:00+01:00\" publishDate=\"2015-03-06T02:00:00+01:00\" urn=\"urn:vpro:media:program:3\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <credits/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:group:100\" midRef=\"g1\" type=\"SERIES\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:program:1\" midRef=\"m1\" type=\"PROGRAM\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:program:2\" midRef=\"m2\" type=\"BROADCAST\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:program:3\" midRef=\"m3\" type=\"PROGRAM\"/>\n" +
                "    <memberOf highlighted=\"false\" midRef=\"m1\" index=\"1\" type=\"PROGRAM\" urnRef=\"urn:vpro:media:program:1\">\n" +
                "        <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\">\n" +
                "            <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "            <memberOf midRef=\"m2\" type=\"BROADCAST\" index=\"1\">\n" +
                "                <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "                <episodeOf midRef=\"g1\" type=\"SERIES\" index=\"1\">\n" +
                "                    <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"2\" circular=\"true\"/>\n" +
                "                </episodeOf>\n" +
                "            </memberOf>\n" +
                "        </memberOf>\n" +
                "    </memberOf>\n" +
                "    <memberOf highlighted=\"false\" midRef=\"m2\" index=\"1\" type=\"BROADCAST\" urnRef=\"urn:vpro:media:program:2\">\n" +
                "        <episodeOf midRef=\"g1\" type=\"SERIES\" index=\"1\">\n" +
                "            <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"2\">\n" +
                "                <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\">\n" +
                "                    <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "                </memberOf>\n" +
                "                <memberOf midRef=\"m2\" type=\"BROADCAST\" index=\"1\" circular=\"true\"/>\n" +
                "            </memberOf>\n" +
                "        </episodeOf>\n" +
                "        <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\">\n" +
                "            <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\">\n" +
                "                <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "                <memberOf midRef=\"m2\" type=\"BROADCAST\" index=\"1\" circular=\"true\"/>\n" +
                "            </memberOf>\n" +
                "        </memberOf>\n" +
                "    </memberOf>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <scheduleEvents/>\n" +
                "    <segments/>\n" +
                "</program>");
    }
    @Test
    public void marshalm2() {
         JAXBTestUtil.roundTripAndSimilar(m2,
            "<program xmlns=\"urn:vpro:media:2009\" type=\"BROADCAST\" embeddable=\"true\" mid=\"m2\" sortDate=\"2015-03-06T00:00:00+01:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2015-03-06T00:00:00+01:00\" lastModified=\"2015-03-06T01:00:00+01:00\" publishDate=\"2015-03-06T02:00:00+01:00\" urn=\"urn:vpro:media:program:2\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <credits/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:group:100\" midRef=\"g1\" type=\"SERIES\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:program:1\" midRef=\"m1\" type=\"PROGRAM\"/>\n" +
                "    <memberOf highlighted=\"false\" midRef=\"m1\" index=\"1\" type=\"PROGRAM\" urnRef=\"urn:vpro:media:program:1\">\n" +
                "        <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\">\n" +
                "            <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "            <memberOf midRef=\"m2\" type=\"BROADCAST\" index=\"1\">\n" +
                "                <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "                <episodeOf midRef=\"g1\" type=\"SERIES\" index=\"1\">\n" +
                "                    <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"2\" circular=\"true\"/>\n" +
                "                </episodeOf>\n" +
                "            </memberOf>\n" +
                "        </memberOf>\n" +
                "    </memberOf>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <scheduleEvents/>\n" +
                "    <episodeOf highlighted=\"false\" midRef=\"g1\" index=\"1\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:100\">\n" +
                "        <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"2\">\n" +
                "            <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\">\n" +
                "                <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "            </memberOf>\n" +
                "            <memberOf midRef=\"m2\" type=\"BROADCAST\" index=\"1\">\n" +
                "                <memberOf midRef=\"m1\" type=\"PROGRAM\" index=\"1\">\n" +
                "                    <memberOf midRef=\"m3\" type=\"PROGRAM\" index=\"1\" circular=\"true\"/>\n" +
                "                </memberOf>\n" +
                "                <episodeOf midRef=\"g1\" type=\"SERIES\" index=\"1\" circular=\"true\"/>\n" +
                "            </memberOf>\n" +
                "        </memberOf>\n" +
                "    </episodeOf>\n" +
                "    <segments/>\n" +
                "</program>");
    }


    @Test
    public void marshalm4() {

        Program program = JAXBTestUtil.roundTripAndSimilar(m4,
            "<program xmlns=\"urn:vpro:media:2009\" type=\"BROADCAST\" embeddable=\"true\" mid=\"m3\" sortDate=\"2015-03-06T00:00:00+01:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2015-03-06T00:00:00+01:00\" lastModified=\"2015-03-06T01:00:00+01:00\" publishDate=\"2015-03-06T02:00:00+01:00\" urn=\"urn:vpro:media:program:3\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <credits/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:group:101\" midRef=\"g2\" type=\"SEASON\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:segment:4\" midRef=\"s1\" type=\"SEGMENT\"/>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <scheduleEvents/>\n" +
                "    <episodeOf highlighted=\"false\" midRef=\"g2\" index=\"1\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:101\">\n" +
                "        <memberOf midRef=\"s1\" type=\"SEGMENT\" index=\"1\">\n" +
                "            <segmentOf midRef=\"m3\" type=\"BROADCAST\">\n" +
                "                <episodeOf midRef=\"g2\" type=\"SEASON\" index=\"1\" circular=\"true\"/>\n" +
                "            </segmentOf>\n" +
                "        </memberOf>\n" +
                "    </episodeOf>\n" +
                "    <segments/>\n" +
                "</program>");
        assertThat(program.getEpisodeOf().first().getChildMid()).isEqualTo("m3");
        assertThat(program.getEpisodeOf().first().getMemberOf().first().getChildMid()).isEqualTo("g2");

    }

    @Test
    public void marshalm4json() {

        Program program = Jackson2TestUtil.roundTripAndSimilar(m4,
            "{\n" +
                "  \"objectType\" : \"program\",\n" +
                "  \"mid\" : \"m3\",\n" +
                "  \"type\" : \"BROADCAST\",\n" +
                "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
                "  \"sortDate\" : 1425596400000,\n" +
                "  \"creationDate\" : 1425596400000,\n" +
                "  \"lastModified\" : 1425600000000,\n" +
                "  \"urn\" : \"urn:vpro:media:program:3\",\n" +
                "  \"embeddable\" : true,\n" +
                "  \"episodeOf\" : [ {\n" +
                "    \"midRef\" : \"g2\",\n" +
                "    \"urnRef\" : \"urn:vpro:media:group:101\",\n" +
                "    \"type\" : \"SEASON\",\n" +
                "    \"index\" : 1,\n" +
                "    \"highlighted\" : false,\n" +
                "    \"memberOf\" : [ {\n" +
                "      \"midRef\" : \"s1\",\n" +
                "      \"type\" : \"SEGMENT\",\n" +
                "      \"segmentOf\" : {\n" +
                "        \"midRef\" : \"m3\",\n" +
                "        \"type\" : \"BROADCAST\",\n" +
                "        \"episodeOf\" : [ {\n" +
                "          \"midRef\" : \"g2\",\n" +
                "          \"type\" : \"SEASON\",\n" +
                "          \"index\" : 1,\n" +
                "          \"circular\" : true\n" +
                "        } ]\n" +
                "      },\n" +
                "      \"index\" : 1\n" +
                "    } ],\n" +
                "    \"episodeOf\" : [ ]\n" +
                "  } ],\n" +
                "  \"broadcasters\" : [ ],\n" +
                "  \"genres\" : [ ],\n" +
                "  \"countries\" : [ ],\n" +
                "  \"languages\" : [ ],\n" +
                "  \"descendantOf\" : [ {\n" +
                "    \"midRef\" : \"g2\",\n" +
                "    \"urnRef\" : \"urn:vpro:media:group:101\",\n" +
                "    \"type\" : \"SEASON\"\n" +
                "  }, {\n" +
                "    \"midRef\" : \"s1\",\n" +
                "    \"urnRef\" : \"urn:vpro:media:segment:4\",\n" +
                "    \"type\" : \"SEGMENT\"\n" +
                "  } ],\n" +
                "  \"publishDate\" : 1425603600000\n" +
                "}");

        MemberRef firstEpisodeOf = program.getEpisodeOf().first();
        assertThat(program.getMid()).isEqualTo("m3");
        assertThat(firstEpisodeOf.getMemberOf().first().getChildMid()).isEqualTo("g2");
        assertThat(firstEpisodeOf.getChildMid()).isEqualTo("m3");
    }



}
