/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rico Jansen
 * @since 3.1
 */
public class HasPredictionConstraintTest {

    @Test
    public void testGetValue() {
        HasPredictionConstraint in = new HasPredictionConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasPredictionConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    public void testApplyTrue() {
        Program program = MediaTestDataBuilder.program().withPredictions().build();
        assertThat(new HasPredictionConstraint().test(program)).isTrue();
    }

    @Test
    public void testApplyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasPredictionConstraint().test(program)).isFalse();
    }

    @Test
    public void testGetESPath() {
        assertThat(new HasPredictionConstraint().getESPath()).isEqualTo("predictions.platform");
    }
}
