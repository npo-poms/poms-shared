/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Date;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class RestrictionTest extends ObjectTest<Restriction> {

    @DataPoint
    public static Restriction nullArgument = null;

    @DataPoint
    public static Restriction withStartAndStop = new TestRestriction(null, new Date(0), new Date(2));

    @DataPoint
    public static Restriction persistedWithStartAndStop = new TestRestriction(1l, new Date(0), new Date(2));

    @DataPoint
    public static Restriction persistedWithStartUpdate = new TestRestriction(1l, new Date(1), new Date(2));

    @Test
    public void testEqualsWhenIncomingWithNullId() throws Exception {
        assertThat(withStartAndStop).isEqualTo(persistedWithStartAndStop);
    }

    private static class TestRestriction extends Restriction {
        private TestRestriction(Long id, Date start, Date stop) {
            super(id, start, stop);
        }
    }
}
