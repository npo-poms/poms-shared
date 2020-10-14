package nl.vpro.domain.media;

import java.time.LocalDateTime;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class MemberRefTest {

    MemberRef ref = new MemberRef();
    {
        ref.setAdded(LocalDateTime.of(2017, 5, 24, 16, 30).atZone(Schedule.ZONE_ID).toInstant());

        ref.setType(MediaType.SERIES);
        ref.setUrnRef("urn:media:123");
        ref.setNumber(1);
        ref.setMidRef("MID_123");
        ref.setHighlighted(true);
        ref.setEpisodeOf(new TreeSet<>());;
        ref.getEpisodeOf()
                .add(RecursiveMemberRef
                    .builder()
                    .parentType(MediaType.SEASON)
                .index(1)
                .highlighted(true)
                .parentMid("SEASON_1").build());
        ref.setSegmentOf(RecursiveMemberRef.builder()
                .parentMid("program_1234")
                .build());
    }
    @Test
    public void xml() {

        JAXBTestUtil.roundTripAndSimilar(ref,
            "<memberRef xmlns=\"urn:vpro:media:2009\" added=\"2017-05-24T16:30:00+02:00\" highlighted=\"true\" midRef=\"MID_123\" index=\"1\" type=\"SERIES\" urnRef=\"urn:media:123\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <episodeOf midRef=\"SEASON_1\" type=\"SEASON\" index=\"1\" highlighted=\"true\"/>\n" +
                "    <segmentOf midRef=\"program_1234\"/>\n" +
                "</memberRef>\n");
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPublisherInstance(), ref,
                "{\n" +
                    "  \"midRef\" : \"MID_123\",\n" +
                    "  \"urnRef\" : \"urn:media:123\",\n" +
                    "  \"type\" : \"SERIES\",\n" +
                    "  \"index\" : 1,\n" +
                    "  \"highlighted\" : true,\n" +
                    "  \"memberOf\" : [ ],\n" +
                    "  \"episodeOf\" : [ {\n" +
                    "    \"midRef\" : \"SEASON_1\",\n" +
                    "    \"type\" : \"SEASON\",\n" +
                    "    \"index\" : 1,\n" +
                    "    \"highlighted\" : true\n" +
                    "  } ],\n" +
                    "  \"segmentOf\" : {\n" +
                    "    \"midRef\" : \"program_1234\"\n" +
                    "  },\n" +
                    "  \"added\" : 1495636200000\n" +
                    "}");
    }

    @Test
    public void withRecursive() throws JsonProcessingException {

        String example  = "{\n" +
            "    \"midRef\" : \"g2\",\n" +
            "    \"memberOf\" : [ {\n" +
            "      \"midRef\" : \"s1\",\n" +
            "      \"type\" : \"SEGMENT\"\n" +
            "    } ]\n" +
            "  }";

        MemberRef r = Jackson2Mapper.getLenientInstance().readerFor(MemberRef.class).readValue(example);
        assertThat(r.getChildMid()).isNull();
        RecursiveMemberRef firstMemberOf = r.getMemberOf().first();
        assertThat(firstMemberOf.getChildMid()).isEqualTo("g2");

    }
}
