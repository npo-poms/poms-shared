package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import nl.vpro.domain.image.Dimension;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Slf4j
public class DimensionTest {

    @Test
    public void reduce() {
        Dimension reduced = Dimension.of(640, 320).reduce();
        assertThat(reduced.getHeight()).isEqualTo(1);
        assertThat(reduced.getWidth()).isEqualTo(2);
    }
}
