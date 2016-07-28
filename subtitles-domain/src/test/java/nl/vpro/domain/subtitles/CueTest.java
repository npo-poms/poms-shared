package nl.vpro.domain.subtitles;

import java.time.Duration;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class CueTest {
    @Test
    public void parseTimeline() throws Exception {
        String line = "0003 00:02:08:11 00:02:11:06";
        Cue timeLine = Cue.parse(null, line, "bla");
        assertEquals(3, timeLine.getSequence());
        assertEquals((2 * 60 + 8) * 1000L + 110, timeLine.getStart().toMillis());
        assertEquals((2 * 60 + 11) * 1000L + 60, timeLine.getEnd().toMillis());
    }

    @Test
    public void json() throws Exception {
        Cue cue = new Cue("MID_1234", 10, Duration.ofSeconds(20), Duration.ofSeconds(30), "bla bla bla");
        Jackson2TestUtil.roundTripAndSimilar(cue, "{\n" +
            "  \"parent\" : \"MID_1234\",\n" +
            "  \"sequence\" : 10,\n" +
            "  \"start\" : 20000,\n" +
            "  \"end\" : 30000,\n" +
            "  \"content\" : \"bla bla bla\"\n" +
            "}");
    }


    @Test
    public void xml() throws Exception {
        Cue cue = new Cue("MID_1234", 10, Duration.ofSeconds(20), Duration.ofSeconds(30), "bla bla bla");
        JAXBTestUtil.roundTripAndSimilar(cue, "<cue parent=\"MID_1234\" sequence=\"10\" start=\"P0DT0H0M20.000S\" end=\"P0DT0H0M30.000S\" xmlns=\"urn:vpro:media:subtitles:2009\">bla bla bla</cue>");
    }

}
