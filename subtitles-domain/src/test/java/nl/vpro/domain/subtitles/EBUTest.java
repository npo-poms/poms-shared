package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class EBUTest {


    @Test
    public void parse() {
        List<Cue> cuesFromStl = EBU.parse("bla", Duration.ZERO, (timeline) -> Duration.ZERO, getClass().getResourceAsStream("/VPWON_1272504.stl")).collect(Collectors.toList());

        List<Cue> cuesFromSrt = WEBVTTandSRT.parseSRT("bla", getClass().getResourceAsStream("/VPWON_1272504.srt")).collect(Collectors.toList());

        for (int i = 0; i < cuesFromSrt.size(); i ++) {
            assertThat(cuesFromStl.get(i)).isEqualTo(cuesFromSrt.get(i));
        }

    }
}
