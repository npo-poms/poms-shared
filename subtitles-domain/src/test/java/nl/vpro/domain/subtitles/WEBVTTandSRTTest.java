package nl.vpro.domain.subtitles;

import java.util.stream.Stream;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class WEBVTTandSRTTest {


    @Test
    public void test() {

        Stream<Cue> bla = WEBVTTandSRT.parseSRT("bla", getClass().getClassLoader().getResourceAsStream("VPWON_1272504.srt"));

        assertThat(bla).hasSize(181);

    }

}
