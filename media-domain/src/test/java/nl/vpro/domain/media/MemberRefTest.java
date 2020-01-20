package nl.vpro.domain.media;

import java.time.LocalDateTime;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class MemberRefTest {

    MemberRef ref = new MemberRef();
    {
        ref.setAdded(LocalDateTime.of(2017, 5, 24, 16, 30).atZone(Schedule.ZONE_ID).toInstant());

        ref.setType(MediaType.SERIES);
        ref.setNumber(1);
        ref.setMidRef("MID_123");
        ref.setHighlighted(true);
    }
    @Test
    public void xml() {

        JAXBTestUtil.roundTripAndSimilar(ref, "<memberRef added=\"2017-05-24T16:30:00+02:00\" highlighted=\"true\" midRef=\"MID_123\" index=\"1\" type=\"SERIES\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>");
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(ref, "{\n" +
            "  \"midRef\" : \"MID_123\",\n" +
            "  \"type\" : \"SERIES\",\n" +
            "  \"index\" : 1,\n" +
            "  \"highlighted\" : true,\n" +
            "  \"added\" : 1495636200000,\n" +
            "  \"memberOf\" : [ ],\n" +
            "  \"episodeOf\" : [ ]\n" +
            "}");
    }
}
