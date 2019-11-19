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

        Jackson2TestUtil.roundTripAndSimilar(cue, "{\n" +
            "  \"parent\" : \"MID_123\",\n" +
            "  \"sequence\" : 0,\n" +
            "   \"identifier\" : \"cue 1\"," +
            "  \"type\" : \"TRANSLATION\",\n" +
            "  \"content\" : \"bla bla\",\n" +
            "  \"lang\" : \"en-US\"\n" +
            "}");


    }

}
