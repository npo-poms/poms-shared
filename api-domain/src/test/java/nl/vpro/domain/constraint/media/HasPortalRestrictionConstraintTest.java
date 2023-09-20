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
public class HasPortalRestrictionConstraintTest {

    @Test
    public void testGetValue() {
        HasPortalRestrictionConstraint in = new HasPortalRestrictionConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasPortalRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    public void testApplyTrue() {
        Program program = MediaTestDataBuilder.program().withPortalRestrictions().build();
        assertThat(new HasPortalRestrictionConstraint().test(program)).isTrue();
    }

    @Test
    public void testApplyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasPortalRestrictionConstraint().test(program)).isFalse();
    }

    @Test
    public void testGetESPath() {
        assertThat(new HasPortalRestrictionConstraint().getESPath()).isEqualTo("exclusives");
    }
}
