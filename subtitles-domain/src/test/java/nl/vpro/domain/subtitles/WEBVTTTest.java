package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;

import org.junit.Test;

import static nl.vpro.domain.subtitles.SubtitlesUtilTest.getSubtitles;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class WEBVTTTest {



    @Test
    public void toWEBVTT() throws IOException {
        assertThat(WEBVTT.formatVVT(SubtitlesUtil.parse(getSubtitles()).findFirst().get(), new StringBuilder()).toString()).isEqualTo("1\n" +
            "2:02.200 --> 2:04.150\n" +
            "888\n" +
            "\n" +
            "");
    }

    @Test
    public void parseTimeLine() throws ParseException {
        String timeLine = "2:02.200 --> 2:04.150";

        Cue cue = WEBVTT.parseCue("parent", "1", timeLine, "bla bla");

        assertThat(cue.getStart()).isEqualTo(Duration.parse("PT2M2.2S"));


    }

}
