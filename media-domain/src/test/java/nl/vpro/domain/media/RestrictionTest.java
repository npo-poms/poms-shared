/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.meeuw.util.test.BasicObjectTheory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class RestrictionTest implements BasicObjectTheory<RestrictionTest.TestRestriction> {

    public static TestRestriction nullArgument = null;
    public static TestRestriction withStartAndStop = new TestRestriction(null, Instant.EPOCH, Instant.ofEpochMilli(2));
    public static TestRestriction persistedWithStartAndStop = new TestRestriction(1L, Instant.EPOCH, Instant.ofEpochMilli(2));
    public static TestRestriction persistedWithStartUpdate = new TestRestriction(1L, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));

    @Test
    public void testEqualsWhenIncomingWithNullId() {
        assertThat(withStartAndStop).isEqualTo(persistedWithStartAndStop);
    }

    @Override
    public Arbitrary<TestRestriction> datapoints() {
        return Arbitraries.of(
            nullArgument,
            withStartAndStop,
            persistedWithStartAndStop,
            persistedWithStartUpdate
        );
    }

    public static class TestRestriction extends Restriction<TestRestriction> {
        private TestRestriction(Long id, Instant start, Instant stop) {
            super(id, start, stop);
        }
    }
}
