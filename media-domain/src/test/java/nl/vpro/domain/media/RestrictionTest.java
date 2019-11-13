/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class RestrictionTest extends ObjectTest<Restriction> {

    @DataPoint
    public static Restriction nullArgument = null;

    @DataPoint
    public static Restriction withStartAndStop = new TestRestriction(null, Instant.EPOCH, Instant.ofEpochMilli(2));

    @DataPoint
    public static Restriction persistedWithStartAndStop = new TestRestriction(1L, Instant.EPOCH, Instant.ofEpochMilli(2));

    @DataPoint
    public static Restriction persistedWithStartUpdate = new TestRestriction(1L, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));

    @Test
    public void testEqualsWhenIncomingWithNullId() {
        assertThat(withStartAndStop).isEqualTo(persistedWithStartAndStop);
    }

    private static class TestRestriction extends Restriction {
        private TestRestriction(Long id, Instant start, Instant stop) {
            super(id, start, stop);
        }
    }
}
