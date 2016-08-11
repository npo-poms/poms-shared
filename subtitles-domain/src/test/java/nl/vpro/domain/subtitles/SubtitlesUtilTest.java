package nl.vpro.domain.subtitles;

import java.io.*;
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



    protected static Subtitles getSubtitles() throws IOException {
        InputStream example = SubtitlesUtilTest.class.getResourceAsStream("/PRID_VPRO_1140017.txt");
        return SubtitlesUtil.ebu("VPRO_1140017", Duration.ofMillis(2 * 60 * 1000), SubtitlesUtil.DUTCH, example);
    }

    @Test
    public void toEBU() throws IOException {
        assertThat(EBU.format(SubtitlesUtil.parse(getSubtitles()).findFirst().get(), new StringBuilder()).toString()).isEqualTo("0001 2:02 2:04\n" +
            "888\n" +
            "\n");

    }

    @Test
    public void toWEBTTVtoEBU() throws IOException {
        InputStream example = SubtitlesUtilTest.class.getResourceAsStream("/POW_00943209.utf8.txt");
        StringWriter w = new StringWriter();
        IOUtils.copy(new InputStreamReader(example, "UTF-8"), w);
        Subtitles subtitles = new Subtitles("POW_00943209", Duration.ofMinutes(2), SubtitlesUtil.DUTCH, SubtitlesFormat.EBU, w.toString());
        subtitles.setType(SubtitlesType.CAPTION);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SubtitlesUtil.toEBU(SubtitlesUtil.standaloneStream(subtitles).iterator(), System.out);
    }



}
