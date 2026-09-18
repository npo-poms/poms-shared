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
class HasPortalRestrictionConstraintTest {

    @Test
    void getValue() {
        HasPortalRestrictionConstraint in = new HasPortalRestrictionConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasPortalRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    void applyTrue() {
        Program program = MediaTestDataBuilder.program().withPortalRestrictions().build();
        assertThat(new HasPortalRestrictionConstraint().test(program)).isTrue();
    }

    @Test
    void applyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasPortalRestrictionConstraint().test(program)).isFalse();
    }

    @Test
    void getESPath() {
        assertThat(new HasPortalRestrictionConstraint().getESPath()).isEqualTo("exclusives");
    }
}
