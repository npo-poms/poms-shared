package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;

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
        IntegerArbitrary x = Arbitraries.integers().between(1, 4);
        IntegerArbitrary y = Arbitraries.integers().between(1, 4);

        return Combinators.combine(
            x.injectNull(0.01),
            y.injectNull(0.01))
            .flatAs(
                (a, b) -> Arbitraries.of(
                    Dimension.of(
                        a == null ? null : a * 100,
                        b == null ? null : b * 100))
            ).injectNull(0.001);

    }
}
