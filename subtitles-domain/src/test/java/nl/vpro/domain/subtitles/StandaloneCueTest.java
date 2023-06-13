package nl.vpro.domain.subtitles;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class StandaloneCueTest {

    @Test
    public void json() {
        StandaloneCue cue = new StandaloneCue(Cue.forMid("MID_123")
            .content("bla bla")
            .identifier("cue 1")
            .sequence(0).build(),
            Locale.US, SubtitlesType.TRANSLATION);

        Jackson2TestUtil.roundTripAndSimilar(cue, """
            {
              "parent" : "MID_123",
              "sequence" : 0,
               "identifier" : "cue 1",  "type" : "TRANSLATION",
              "content" : "bla bla",
              "lang" : "en-US"
            }""");


    }

}
