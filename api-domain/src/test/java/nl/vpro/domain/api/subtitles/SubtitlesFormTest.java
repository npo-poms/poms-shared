package nl.vpro.domain.api.subtitles;

import org.junit.Test;

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
        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : \"bla\",\n" +
            "    \"types\" : \"CAPTION\"\n" +
            "  }\n" +
            "}");
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
        JAXBTestUtil.roundTripAndSimilar(form, "<api:subtitlesForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:text>bla</api:text>\n" +
            "        <api:types match=\"MUST\">\n" +
            "            <api:matcher>CAPTION</api:matcher>\n" +
            "        </api:types>\n" +
            "    </api:searches>\n" +
            "</api:subtitlesForm>");

    }
}
