package nl.vpro.domain;

import net.jqwik.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.meeuw.theories.BasicObjectTheory;

import com.google.common.collect.Range;

class BasicEmbargoTest implements BasicObjectTheory<BasicEmbargo> {

    @Override
    public Arbitrary<Object> datapoints() {
        Arbitrary<Instant> instances = Arbitraries.integers()
            .between(-100_000, 100_000)
            .withDistribution(RandomDistribution.gaussian())
            .map(i -> Instant.now().truncatedTo(ChronoUnit.MINUTES).plus(Duration.ofDays(i)))
            .injectNull(0.01);

        return Combinators.combine(instances, instances)
            .as(BasicEmbargo::new);
    }

    @Property
    public void asRange(@ForAll(DATAPOINTS) Object object) {
        BasicEmbargo basicEmbargo = (BasicEmbargo) object;
        Range<Instant> asRange = basicEmbargo.asRange();
        System.out.printf("%s -> %s%n", basicEmbargo, asRange);
    }
}
