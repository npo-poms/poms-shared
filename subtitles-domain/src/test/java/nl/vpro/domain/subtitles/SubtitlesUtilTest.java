package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class SubtitlesUtilTest {


    @Test
    public void basic() throws IOException {
        List<Cue> list = SubtitlesUtil.parse(getSubtitles()).collect(Collectors.toList());
        assertThat(list).hasSize(403);
        assertThat(list.get(0).getContent()).isEqualTo("888");
        assertThat(list.get(199).getContent()).isEqualTo(
            "Nee? Nee.\nHoe ziet-ie eruit?");

    }



    protected Subtitles getSubtitles() throws IOException {
        InputStream example = getClass().getResourceAsStream("/PRID_VPRO_1140017.txt");
        return SubtitlesUtil.ebu("VPRO_1140017", Duration.ofMillis(2 * 60 * 1000), example);
    }

    @Test
    public void toEBU() throws IOException {
        assertThat(SubtitlesUtil.formatEBU(SubtitlesUtil.parse(getSubtitles()).findFirst().get(), new StringBuilder()).toString()).isEqualTo("\"0001 2:02 2:04\n" +
            "888\n" +
            "\n");

    }

}
