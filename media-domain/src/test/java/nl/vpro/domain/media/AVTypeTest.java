package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AVTypeTest {

    @Test
    void test() {

        assertThat(AVType.AUDIO.test(MediaType.VISUALRADIO)).isFalse();
    }
}
