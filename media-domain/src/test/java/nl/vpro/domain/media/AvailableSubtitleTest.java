package nl.vpro.domain.media;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class AvailableSubtitleTest {

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

}
