package nl.vpro.poms.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {


    @Test
    void escape() {
        assertThat(Headers.escapeHeaderValue( "foo bar: 汉")).isEqualTo("foo bar: \\u6C49");

    }

}
