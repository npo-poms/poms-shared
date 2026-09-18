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
 * @author Michiel Meeuwissen
 * @since 3.3.0
 */
class HasPortalConstraintTest {

    @Test
    void getValue() {
        HasPortalConstraint in = new HasPortalConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasPortalConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    void applyTrue() {
        Program program = MediaTestDataBuilder.program().withPortals().build();
        assertThat(new HasPortalConstraint().test(program)).isTrue();
    }

    @Test
    void applyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasPortalConstraint().test(program)).isFalse();
    }

    @Test
    void getESPath() {
        assertThat(new HasPortalConstraint().getESPath()).isEqualTo("portals.id");
    }
}
