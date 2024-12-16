package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

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
    public Arbitrary<Dimension> datapoints() {
        IntegerArbitrary x = Arbitraries.integers().between(1, 1000);
        IntegerArbitrary y = Arbitraries.integers().between(1, 1000);

        return Combinators.combine(
            x.injectNull(0.01),
            y.injectNull(0.01))
            .flatAs(
                (a, b) -> Arbitraries.of(
                    Dimension.of(a, b)
                )
            );
    }

    @Override
    public Arbitrary<Tuple.Tuple2<Dimension, Dimension>> equalDatapoints() {

        return Arbitraries.of(
            Tuple.of(
                Dimension.of(640, 320),
                Dimension.of(640, 320)
            ),
            Tuple.of(
                Dimension.of(null, 320),
                Dimension.of(null, 320)
            )
        );
    }

}
