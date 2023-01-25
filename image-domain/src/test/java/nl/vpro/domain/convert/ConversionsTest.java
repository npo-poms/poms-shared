package nl.vpro.domain.convert;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.image.Dimension;

import static org.assertj.core.api.Assertions.assertThat;


class ConversionsTest {

    @Test
    void predictDimensions() {

        assertThat(Conversions.predictDimensions(Dimension.of(2048, 1360), "s150")).isEqualTo(Dimension.of(150, 100));

        assertThat(Conversions.predictDimensions(Dimension.of(2048, 1360), "s3000>")).isEqualTo(Dimension.of(2048, 1360));
    }
}
