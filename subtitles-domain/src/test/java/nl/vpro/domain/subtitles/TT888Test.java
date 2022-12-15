package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class TT888Test {
    @Test
    public void parseTimeline() {
        String line = "0003 00:02:08:11 00:02:11:06";
        TimeLine timeLine = TT888.parseTimeline(line);
        assertThat(timeLine.getSequence()).isEqualTo(3);
        assertThat(timeLine.getStart()).isEqualTo(Duration.ofMillis((2 * 60 + 8) * 1000L + 110));
        assertThat(timeLine.getEnd()).isEqualTo(Duration.ofMillis((2 * 60 + 11) * 1000L + 60));
    }


    @Test
    public void readWithNewLlines() {
        Stream<Cue> cues = TT888.parse("NPS_1145938", Duration.ZERO, (tl) -> Duration.ZERO, getClass().getResourceAsStream("/NPS_1145938.txt"));
        List<Cue> cueList = new ArrayList<>();
        cues.forEach(cueList::add);
        assertThat(cueList.get(0).getContent()).isEqualTo("888");
        assertThat(cueList.get(1).getContent()).isEqualTo("Als moeder van twee kinderen\n" +
            "vraag ik me af...");
    }

    @Test
    public void readWithEncoding() {
        Stream<Cue> cues = TT888.parse("POW_04322816", Duration.ZERO, (tl) -> Duration.ZERO, getClass().getResourceAsStream("/POW_04322816.txt"));
        List<Cue> cueList = new ArrayList<>();
        cues.forEach(cueList::add);
        assertThat(cueList.get(160).getContent()).startsWith("Geachte heer D¼r¼st, bij deze delen");
    }

    @Test
    public void readWithEncodingUTF8() {
        Stream<Cue> cues = TT888.parseUTF8("POW_04322816", Duration.ZERO, (tl) -> Duration.ZERO, getClass().getResourceAsStream("/POW_04322816.txt"));
        List<Cue> cueList = new ArrayList<>();
        cues.forEach(cueList::add);
        assertThat(cueList.get(160).getContent()).startsWith("Geachte heer Dürüst, bij deze delen");
    }

    @Test
    public void readKN_1729896() {
        Stream<Cue> cues = TT888.parseUTF8("KN_1729896", Duration.ZERO, (tl) -> Duration.ZERO, getClass().getResourceAsStream("/KN_1729896.txt"));
        List<Cue> cueList = new ArrayList<>();
        cues.forEach(cueList::add);
        log.info("{}", cueList);
        assertThat(cueList.get(126).getContent()).startsWith("You can find ING's response on pointer.nl.");
    }
}
