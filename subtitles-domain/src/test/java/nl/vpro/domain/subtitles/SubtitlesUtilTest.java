package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
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
        StringWriter w = new StringWriter();
        IOUtils.copy(new InputStreamReader(example, "ISO-6937"), w);
        return new Subtitles("VPRO_1140017", Duration.ofMillis(2 * 60 * 1000), w.toString());
    }

}
