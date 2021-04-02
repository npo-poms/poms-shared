package nl.vpro.domain.api;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.8
 */
public class TextMatcherListTest {

    @Test
    public void marshal() {
        final TextMatcherList textMatcherList = new TextMatcherList(
                Arrays.asList(new TextMatcher("a", Match.SHOULD), new TextMatcher("b", Match.SHOULD)), Match.MUST);
        TextMatcherList result = JAXBTestUtil.roundTripAndSimilar(textMatcherList,
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                        + "<local:textMatcherList match=\"MUST\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n"
                        + "    <api:matcher match=\"SHOULD\">a</api:matcher>\n"
                        + "    <api:matcher match=\"SHOULD\">b</api:matcher>\n" + "</local:textMatcherList>");

        assertThat(result.asList().get(0).getMatch()).isEqualTo(Match.SHOULD);
    }

}
