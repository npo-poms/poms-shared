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
            """
                <program xmlns="urn:vpro:media:2009" embeddable="true" mid="m3" sortDate="2015-03-06T00:00:00+01:00" workflow="FOR PUBLICATION" creationDate="2015-03-06T00:00:00+01:00" lastModified="2015-03-06T01:00:00+01:00" publishDate="2015-03-06T02:00:00+01:00" urn="urn:vpro:media:program:3" xmlns:shared="urn:vpro:shared:2009">
                    <credits/>
                    <descendantOf urnRef="urn:vpro:media:group:100" midRef="g1" type="SERIES"/>
                    <descendantOf urnRef="urn:vpro:media:program:1" midRef="m1" type="PROGRAM"/>
                    <descendantOf urnRef="urn:vpro:media:program:2" midRef="m2" type="BROADCAST"/>
                    <descendantOf urnRef="urn:vpro:media:program:3" midRef="m3" type="PROGRAM"/>
                    <memberOf highlighted="false" midRef="m1" index="1" type="PROGRAM" urnRef="urn:vpro:media:program:1">
                        <memberOf midRef="m3" type="PROGRAM" index="1">
                            <memberOf midRef="m1" type="PROGRAM" index="1" circular="true"/>
                            <memberOf midRef="m2" type="BROADCAST" index="1">
                                <memberOf midRef="m1" type="PROGRAM" index="1" circular="true"/>
                                <episodeOf midRef="g1" type="SERIES" index="1">
                                    <memberOf midRef="m3" type="PROGRAM" index="2" circular="true"/>
                                </episodeOf>
                            </memberOf>
                        </memberOf>
                    </memberOf>
                    <memberOf highlighted="false" midRef="m2" index="1" type="BROADCAST" urnRef="urn:vpro:media:program:2">
                        <episodeOf midRef="g1" type="SERIES" index="1">
                            <memberOf midRef="m3" type="PROGRAM" index="2">
                                <memberOf midRef="m1" type="PROGRAM" index="1">
                                    <memberOf midRef="m3" type="PROGRAM" index="1" circular="true"/>
                                </memberOf>
                                <memberOf midRef="m2" type="BROADCAST" index="1" circular="true"/>
                            </memberOf>
                        </episodeOf>
                        <memberOf midRef="m1" type="PROGRAM" index="1">
                            <memberOf midRef="m3" type="PROGRAM" index="1">
                                <memberOf midRef="m1" type="PROGRAM" index="1" circular="true"/>
                                <memberOf midRef="m2" type="BROADCAST" index="1" circular="true"/>
                            </memberOf>
                        </memberOf>
                    </memberOf>
                    <locations/>
                    <images/>
                    <scheduleEvents/>
                    <segments/>
                </program>""");
    }
    @Test
    public void marshalm2() {
         JAXBTestUtil.roundTripAndSimilar(m2,
             """
                 <program xmlns="urn:vpro:media:2009" type="BROADCAST" embeddable="true" mid="m2" sortDate="2015-03-06T00:00:00+01:00" workflow="FOR PUBLICATION" creationDate="2015-03-06T00:00:00+01:00" lastModified="2015-03-06T01:00:00+01:00" publishDate="2015-03-06T02:00:00+01:00" urn="urn:vpro:media:program:2" xmlns:shared="urn:vpro:shared:2009">
                     <credits/>
                     <descendantOf urnRef="urn:vpro:media:group:100" midRef="g1" type="SERIES"/>
                     <descendantOf urnRef="urn:vpro:media:program:1" midRef="m1" type="PROGRAM"/>
                     <memberOf highlighted="false" midRef="m1" index="1" type="PROGRAM" urnRef="urn:vpro:media:program:1">
                         <memberOf midRef="m3" type="PROGRAM" index="1">
                             <memberOf midRef="m1" type="PROGRAM" index="1" circular="true"/>
                             <memberOf midRef="m2" type="BROADCAST" index="1">
                                 <memberOf midRef="m1" type="PROGRAM" index="1" circular="true"/>
                                 <episodeOf midRef="g1" type="SERIES" index="1">
                                     <memberOf midRef="m3" type="PROGRAM" index="2" circular="true"/>
                                 </episodeOf>
                             </memberOf>
                         </memberOf>
                     </memberOf>
                     <locations/>
                     <images/>
                     <scheduleEvents/>
                     <episodeOf highlighted="false" midRef="g1" index="1" type="SERIES" urnRef="urn:vpro:media:group:100">
                         <memberOf midRef="m3" type="PROGRAM" index="2">
                             <memberOf midRef="m1" type="PROGRAM" index="1">
                                 <memberOf midRef="m3" type="PROGRAM" index="1" circular="true"/>
                             </memberOf>
                             <memberOf midRef="m2" type="BROADCAST" index="1">
                                 <memberOf midRef="m1" type="PROGRAM" index="1">
                                     <memberOf midRef="m3" type="PROGRAM" index="1" circular="true"/>
                                 </memberOf>
                                 <episodeOf midRef="g1" type="SERIES" index="1" circular="true"/>
                             </memberOf>
                         </memberOf>
                     </episodeOf>
                     <segments/>
                 </program>""");
    }


    @Test
    public void marshalm4() {

        Program program = JAXBTestUtil.roundTripAndSimilar(m4,
            """
                <program xmlns="urn:vpro:media:2009" type="BROADCAST" embeddable="true" mid="m3" sortDate="2015-03-06T00:00:00+01:00" workflow="FOR PUBLICATION" creationDate="2015-03-06T00:00:00+01:00" lastModified="2015-03-06T01:00:00+01:00" publishDate="2015-03-06T02:00:00+01:00" urn="urn:vpro:media:program:3" xmlns:shared="urn:vpro:shared:2009">
                    <credits/>
                    <descendantOf urnRef="urn:vpro:media:group:101" midRef="g2" type="SEASON"/>
                    <descendantOf urnRef="urn:vpro:media:segment:4" midRef="s1" type="SEGMENT"/>
                    <locations/>
                    <images/>
                    <scheduleEvents/>
                    <episodeOf highlighted="false" midRef="g2" index="1" type="SEASON" urnRef="urn:vpro:media:group:101">
                        <memberOf midRef="s1" type="SEGMENT" index="1">
                            <segmentOf midRef="m3" type="BROADCAST">
                                <episodeOf midRef="g2" type="SEASON" index="1" circular="true"/>
                            </segmentOf>
                        </memberOf>
                    </episodeOf>
                    <segments/>
                </program>""");
        assertThat(program.getEpisodeOf().first().getChildMid()).isEqualTo("m3");
        assertThat(program.getEpisodeOf().first().getMemberOf().first().getChildMid()).isEqualTo("g2");

    }

    @Test
    public void marshalm4json() {

        Program program = Jackson2TestUtil.roundTripAndSimilar(m4,
            """
                {
                  "objectType" : "program",
                  "mid" : "m3",
                  "type" : "BROADCAST",
                  "workflow" : "FOR_PUBLICATION",
                  "sortDate" : 1425596400000,
                  "creationDate" : 1425596400000,
                  "lastModified" : 1425600000000,
                  "urn" : "urn:vpro:media:program:3",
                  "embeddable" : true,
                  "episodeOf" : [ {
                    "midRef" : "g2",
                    "urnRef" : "urn:vpro:media:group:101",
                    "type" : "SEASON",
                    "index" : 1,
                    "highlighted" : false,
                    "memberOf" : [ {
                      "midRef" : "s1",
                      "type" : "SEGMENT",
                      "segmentOf" : {
                        "midRef" : "m3",
                        "type" : "BROADCAST",
                        "episodeOf" : [ {
                          "midRef" : "g2",
                          "type" : "SEASON",
                          "index" : 1,
                          "circular" : true
                        } ]
                      },
                      "index" : 1
                    } ],
                    "episodeOf" : [ ]
                  } ],
                  "broadcasters" : [ ],
                  "genres" : [ ],
                  "countries" : [ ],
                  "languages" : [ ],
                  "descendantOf" : [ {
                    "midRef" : "g2",
                    "urnRef" : "urn:vpro:media:group:101",
                    "type" : "SEASON"
                  }, {
                    "midRef" : "s1",
                    "urnRef" : "urn:vpro:media:segment:4",
                    "type" : "SEGMENT"
                  } ],
                  "publishDate" : 1425603600000
                }""");

        MemberRef firstEpisodeOf = program.getEpisodeOf().first();
        assertThat(program.getMid()).isEqualTo("m3");
        assertThat(firstEpisodeOf.getMemberOf().first().getChildMid()).isEqualTo("g2");
        assertThat(firstEpisodeOf.getChildMid()).isEqualTo("m3");
    }



}
