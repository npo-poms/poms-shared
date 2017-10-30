package nl.vpro.domain.media;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class MemberRefTest {

    @Test
    public void xml() throws IOException, SAXException {
        MemberRef ref = new MemberRef();
        ref.setAdded(Instant.EPOCH);
        JAXBTestUtil.roundTripAndSimilar(ref, "<memberRef added=\"1970-01-01T01:00:00+01:00\" highlighted=\"false\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>");
    }

    @Test
    public void json() throws Exception {
        MemberRef ref = new MemberRef();
        ref.setAdded(LocalDateTime.of(2017, 5, 24, 16, 30).atZone(Schedule.ZONE_ID).toInstant());
        Jackson2TestUtil.roundTripAndSimilar(ref, "{\n" +
            "  \"highlighted\" : false,\n" +
            "  \"added\" : 1495636200000\n" +
            "}");
    }
}
