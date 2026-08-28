package nl.vpro.domain.subtitles;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import nl.vpro.logging.Log4j2OutputStream;
import nl.vpro.util.CountedIterator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Log4j2
public class WEBVTTandSRTTest {


    @Test
    public void test() {
        Stream<Cue> bla = WEBVTTandSRT.parseSRT("bla", getClass().getClassLoader().getResourceAsStream("VPWON_1272504.srt"));
        assertThat(bla).hasSize(181);
    }


    @Test
    public void MSE4363() throws IOException {
        Subtitles subtitles = Subtitles.builder().value(getClass().getResourceAsStream("/WO_NTR_15099292.srt")).format(SubtitlesFormat.SRT).build();
        CountedIterator<StandaloneCue> parsed = SubtitlesUtil.standaloneIterator(subtitles, true, true);
        SubtitlesUtil.stream(parsed, SubtitlesFormat.WEBVTT, Log4j2OutputStream.debug(log));
    }

}
