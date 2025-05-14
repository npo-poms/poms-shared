package nl.vpro.domain.subtitles;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import static nl.vpro.i18n.Locales.DUTCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class SubtitlesUtilTest {


    @Test
    public void basic() throws IOException {
        List<Cue> list = SubtitlesUtil.parse(getSubtitles(), true).collect(Collectors.toList());
        assertThat(list).hasSize(403);
        assertThat(list.getFirst().getContent()).isEqualTo("888");
        assertThat(list.get(199).getContent()).isEqualTo(
            "Nee? Nee.\nHoe ziet-ie eruit?");

    }



    protected static Subtitles getSubtitles() throws IOException {
        InputStream example = SubtitlesUtilTest.class.getResourceAsStream("/PRID_VPRO_1140017.txt");
        return SubtitlesUtil.tt888("VPRO_1140017", Duration.ofMinutes(2), DUTCH, example);
    }
    protected static Subtitles getSubtitlesAr() throws IOException {
        InputStream example = SubtitlesUtilTest.class.getResourceAsStream("/POMS_VPRO_4981202.vtt");
        return SubtitlesUtil.vtt("POMS_VPRO_4981202", Duration.ofMinutes(2), Locale.of("ar"), example);
    }

    @Test
    public void toTT888() throws IOException {
        assertThat(TT888.format(SubtitlesUtil
            .parse(getSubtitles(), true)
            .getCues()
            .findFirst()
            .orElseThrow(NullPointerException::new), new StringBuilder()).toString()).isEqualTo("""
            0001 00:02:20 00:04:15
            888

            """);

    }

    @Test
    public void toWEBTTVtoTT888() throws IOException {
        InputStream example = SubtitlesUtilTest.class.getResourceAsStream("/POW_00943209.utf8.txt");
        StringWriter w = new StringWriter();
        IOUtils.copy(new InputStreamReader(example, StandardCharsets.UTF_8), w);
        Subtitles subtitles = Subtitles.builder()
            .mid("POW_00943209")
            .offset(Duration.ofMinutes(2))
            .language(DUTCH)
            .format(SubtitlesFormat.TT888)
            .content(w.toString())
            .type(SubtitlesType.CAPTION)
            .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SubtitlesUtil.toTT888(SubtitlesUtil.standaloneStream(subtitles, true, false).iterator(), System.out);
    }



}
