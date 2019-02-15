package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static nl.vpro.domain.subtitles.SubtitlesUtilTest.getSubtitles;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class WEBVTTTest {



    @Test
    public void toWEBVTTCue() throws IOException {
        assertThat(WEBVTTandSRT.formatCue(
            SubtitlesUtil.parse(getSubtitles(), false).findFirst().get(), new StringBuilder(), ".").toString()).isEqualTo("1\n" +
            "00:00:02.200 --> 00:00:04.150\n" +
            "888\n" +
            "\n" +
            "");
    }

    @Test
    public void toWEBVTT() throws IOException {
        StringWriter writer = new StringWriter();
        WEBVTTandSRT.formatWEBVTT(SubtitlesUtil.standaloneStream(getSubtitles(), false).limit(3).collect(Collectors.toList()).iterator(), writer);
        assertThat(writer.toString()).isEqualTo(
            "WEBVTT\n" +
                "\n" +
                "1\n" +
                "00:00:02.200 --> 00:00:04.150\n" +
                "888\n" +
                "\n" +
                "2\n" +
                "00:00:04.200 --> 00:00:08.060\n" +
                "*'k Heb een paar puntjes die ik met je wil bespreken\n" +
                "\n" +
                "3\n" +
                "00:00:08.110 --> 00:00:11.060\n" +
                "*Dat wil ik doen in jouw mobiele bakkerij\n" +
                "\n" +
                "");
    }

    @Test
    public void parse() {
        String example = "WEBVTT\n" +
            "\n" +
            "1\n" +
            "0:02.200 --> 2:04.150\n" +
            "888\n" +
            "\n" +
            "2\n" +
            "2:04.200 --> 2:08.060\n" +
            "*'k Heb een paar puntjes\n" +
            "die ik met je wil bespreken\n" +
            "\n" +
            "3\n" +
            "2:08.110 --> 2:11.060\n" +
            "*Dat wil ik doen\n" +
            "in jouw mobiele bakkerij\n" +
            "\n";
        List<Cue> cues = WEBVTTandSRT.parse("bla", Duration.ofMinutes(2), new StringReader(example), ".").collect(Collectors.toList());
        assertThat(cues).hasSize(3);
        assertThat(cues.get(0).getSequence()).isEqualTo(1);
        assertThat(cues.get(0).getContent()).isEqualTo("888");


    }

    @Test
    public void parseEmpty() {
        String example = "WEBVTT\n\n";
        List<Cue> cues = WEBVTTandSRT.parse("bla", Duration.ofMinutes(2), new StringReader(example), ".").collect(Collectors.toList());
        assertThat(cues).hasSize(0);
    }

    @Test
    public void parseTimeLine() throws ParseException {
        String timeLine = "2:02.200 --> 2:04.150";

        Cue cue = WEBVTTandSRT.parseCue("parent", "1", Duration.ofMinutes(2), timeLine, "bla bla", ".");

        assertThat(cue.getStart()).isEqualTo(Duration.parse("PT0M2.2S"));
    }

    @Test
    public void parseDuration() {
        Duration duration = WEBVTTandSRT.parseDuration("00:00:20.000", ".");
        assertThat(duration).isEqualTo(Duration.ofSeconds(20));
    }

    @Test
    public void parseDurationWithComma() {
        Duration duration = WEBVTTandSRT.parseDuration("00:00:20,000", ",");
        assertThat(duration).isEqualTo(Duration.ofSeconds(20));
    }

<<<<<<< HEAD
=======
    @Test
    public void parseWithoutCuesWithComments() {
        InputStream example = getClass().getResourceAsStream("/POMS_VPRO_4981202.vtt");
        List<Cue> cues = SubtitlesUtil.fillCueNumber(WEBVTTandSRT.parseWEBVTT("bla", example)).collect(Collectors.toList());

        assertThat(cues).hasSize(430);
        for (Cue cue : cues) {
            assertThat(cue.getSequence()).isNotNull();
        }
    }

    @Test
    public void MSE4363() throws IOException {
        InputStream example = getClass().getResourceAsStream("/WO_NTR_15099292.srt");
        Subtitles subtitles = Subtitles.builder().value(example).format(SubtitlesFormat.SRT).build();
        CountedIterator<StandaloneCue> parsed = SubtitlesUtil.standaloneIterator(subtitles, true, true);
        SubtitlesUtil.stream(parsed, SubtitlesFormat.WEBVTT, System.out);


    }


    @Test
    public void parseWithCommentsAndNewlines() {
        InputStream example = getClass().getResourceAsStream("/WO_NPO_14933889.vtt");
        List<Cue> cues = SubtitlesUtil.fillCueNumber(WEBVTTandSRT.parseWEBVTT("bla", example)).collect(Collectors.toList());

        assertThat(cues).hasSize(10);
        for (Cue cue : cues) {
            log.info("{}:{}", cue, cue.asRange());
            assertThat(cue.getSequence()).isNotNull();
        }
        assertThat(cues.get(1).getSettings().getValue()).isEqualTo("A:start ");
        assertThat(cues.get(6).getContent()).isEqualTo("De afgelopen vier jaar\n" +
            "hebben we een aantal geliefde...");


    }
>>>>>>> 4397357a5... NPA-470 Test case.
}
