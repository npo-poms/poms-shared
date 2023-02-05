package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import org.junit.jupiter.api.Test;
import org.meeuw.util.test.ComparableTheory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Slf4j
public class DimensionTest implements ComparableTheory<Dimension> {

    @Test
    public void reduce() {
        Dimension reduced = Dimension.of(640, 320).reduce();
        assertThat(reduced.getHeight()).isEqualTo(1);
        assertThat(reduced.getWidth()).isEqualTo(2);
    }

    @Override
    public Arbitrary<? extends Dimension> datapoints() {
        return Arbitraries.of(
            Dimension.of(640, 320),
            Dimension.of(640, null),
            Dimension.of((Long) null, null),
            Dimension.of(1, 2)
            ).injectNull(0.2);
    }
}
