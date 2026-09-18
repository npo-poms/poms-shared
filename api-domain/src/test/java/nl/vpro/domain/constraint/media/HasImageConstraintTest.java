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
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
class HasImageConstraintTest {

    @Test
    void getValue() {
        HasImageConstraint in = new HasImageConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasImageConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    void applyTrue() {
        Program program = MediaTestDataBuilder.program().withImages().build();
        assertThat(new HasImageConstraint().test(program)).isTrue();
    }

    @Test
    void applyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasImageConstraint().test(program)).isFalse();
    }

    @Test
    void getESPath() {
        assertThat(new HasImageConstraint().getESPath()).isEqualTo("images.urn");
    }
}
