package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@Slf4j
class RecursiveMemberRefTest {

    Group g1 = MediaTestDataBuilder.series().mid("g1").id(100L).withFixedDates().build();

    Program m1 = MediaTestDataBuilder.program().mid("m1").id(1L).withFixedDates().build();
    Program m2 = MediaTestDataBuilder.broadcast().mid("m2").id(2L).withFixedDates()
        .memberOf(m1)
        .episodeOf(g1, 1)
        .build();
    Program m3 = MediaTestDataBuilder.program().mid("m3").id(3L).withFixedDates()
        .memberOf(m2)
        .memberOf(m1)
        .build();

    {
        // force circular to  reproduce  MSE-4895
        m1.getMemberOf().add(new MemberRef(m1, m3, 1, OwnerType.BROADCASTER));
        g1.getMemberOf().add(new MemberRef(g1, m3, 2, OwnerType.BROADCASTER));

    }


    @Test
    public void circular() {
        SortedSet<MemberRef> memberOf = m3.getMemberOf();
        MemberRef first = memberOf.first();
        log.info("{}", first.getMemberOf());
        assertThat(first.getMemberOf().first().getMemberOf().first().isCircular()).isTrue();
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




}
