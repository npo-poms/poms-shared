package nl.vpro.domain.media.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DescriptionTest {

    @Test
    void strip() {
        assertThat(Description.strip("foo\r\nbar")).isEqualTo("foo\nbar");
        assertThat(Description.strip("foo\rbar")).isEqualTo("foo\nbar");
    }
}
