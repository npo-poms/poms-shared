package nl.vpro.domain.api.subtitles;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.SimpleTextMatcher;
import nl.vpro.domain.api.TextMatcher;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class SubtitlesFormTest {

    @Test
    public void json() {
        SubtitlesForm form = SubtitlesForm.builder()
            .searches(
                SubtitlesSearch.builder().text(
                    SimpleTextMatcher.builder().value("bla").build()
                )
                    .types(TextMatcherList.must(TextMatcher.must("CAPTION")))
                    .build()
            ).build();
        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
              "searches" : {
                "text" : "bla",
                "types" : "CAPTION"
              }
            }""");
    }

    @Test
    public void xml() {
        SubtitlesForm form = SubtitlesForm.builder()
            .searches(
                SubtitlesSearch.builder().text(
                    SimpleTextMatcher.builder().value("bla").build()
                )
                    .types(TextMatcherList.must(TextMatcher.must("CAPTION")))
                    .build()
            ).build();
        JAXBTestUtil.roundTripAndSimilar(form, """
            <api:subtitlesForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:text>bla</api:text>
                    <api:types match="MUST">
                        <api:matcher>CAPTION</api:matcher>
                    </api:types>
                </api:searches>
            </api:subtitlesForm>""");

    }
}
