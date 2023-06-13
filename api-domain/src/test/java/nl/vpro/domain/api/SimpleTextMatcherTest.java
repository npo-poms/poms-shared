package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTextMatcherTest {

    @Test
    public void string() {
        SimpleTextMatcher matcher = new SimpleTextMatcher("foobar");

        assertThat(matcher.toString()).isEqualTo("SimpleTextMatcher{value='foobar', matchType=TEXT}");

    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(SimpleTextMatcher.builder().semantic(true).value("foobar").build(), """
            {
              "value" : "foobar",
              "semantic" : true
            }""");
    }


}
