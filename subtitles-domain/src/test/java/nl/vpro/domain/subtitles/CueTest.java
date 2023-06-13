package nl.vpro.domain.subtitles;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class CueTest {


    @Test
    public void json() {
        Cue cue = Cue.builder().mid("MID_1234")
            .sequence(10)
            .identifier("cue tien")
            .start(Duration.ofSeconds(20))
            .end(Duration.ofSeconds(30))
            .content("bla bla bla")
            .settings(CueSettings.webvtt("A:left"))
            .build();
        Jackson2TestUtil.roundTripAndSimilar(cue, """
            {
              "parent" : "MID_1234",
              "sequence" : 10,
              "identifier" : "cue tien",
              "start" : 20000,
              "end" : 30000,
              "settings" : "A:left",
              "content" : "bla bla bla"
            }""");
    }


    @Test
    public void xml() {
        Cue cue = Cue.forMid("MID_1234")
            .sequence(10)
            .identifier("cue 10")
            .start(Duration.ofSeconds(20))
            .end(Duration.ofSeconds(30))
            .content("bla bla bla")
            .webvttSettings("A:center")
            .build();
        JAXBTestUtil.roundTripAndSimilar(cue, "<subtitles:cue parent=\"MID_1234\" sequence=\"10\" identifier=\"cue 10\" start=\"P0DT0H0M20.000S\" end=\"P0DT0H0M30.000S\" settings=\"A:center\" xmlns:subtitles=\"urn:vpro:media:subtitles:2009\">bla bla bla</subtitles:cue>");
    }

}
