package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTextMatcherTest {

    @Test
    public void string() {
        SimpleTextMatcher matcher = new SimpleTextMatcher("foobar");

        assertThat(matcher.toString()).isEqualTo("SimpleTextMatcher{value='foobar', matchType=TEXT}");

    }

}
