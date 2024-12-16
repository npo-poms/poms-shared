package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.BasicObjectTheory;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class AvailableSubtitleTest implements BasicObjectTheory<AvailableSubtitles> {

    @Test
    public void test() {
        JAXBTestUtil.roundTripAndSimilar(AvailableSubtitles.builder()
                .language(Locale.ENGLISH)
                .type(SubtitlesType.TRANSLATION)
                .workflow(SubtitlesWorkflow.MISSING)
                .build(),
            """
                <local:availableSubtitles language="en" type="TRANSLATION" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:local="uri:local" workflow="MISSING" />
                """);
    }

    @Test
    public void equalsAndHashCode() {
        var en1 = AvailableSubtitles.builder()
                .language(Locale.ENGLISH)
                .type(SubtitlesType.TRANSLATION)
                .workflow(SubtitlesWorkflow.MISSING)
                .build();
        var en2 = AvailableSubtitles.builder()
                .language(Locale.ENGLISH)
                .type(SubtitlesType.TRANSLATION)
                .workflow(SubtitlesWorkflow.FOR_REPUBLICATION)
                .build();

        assertThat(en1).isEqualTo(en2);
    }

    @Override
    public Arbitrary<AvailableSubtitles> datapoints() {
        return Arbitraries.of(
            AvailableSubtitles.builder()
                .language(Locale.ENGLISH)
                .type(SubtitlesType.TRANSLATION)
                .workflow(SubtitlesWorkflow.MISSING)
                .build(),
            AvailableSubtitles.builder()
                .language(Locale.ENGLISH)
                .type(SubtitlesType.TRANSLATION)
                .workflow(SubtitlesWorkflow.FOR_REPUBLICATION)
                .build(),
            AvailableSubtitles.builder()
                .language(Locales.NETHERLANDISH)
                .type(SubtitlesType.CAPTION)
                .build(),
            AvailableSubtitles.builder()
                .language(Locale.JAPANESE)
                .type(SubtitlesType.CAPTION)
                .build()
        );

    }
}
