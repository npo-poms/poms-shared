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
public class HasGeoRestrictionConstraintTest {

    @Test
    public void testGetValue() {
        HasGeoRestrictionConstraint in = new HasGeoRestrictionConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasGeoRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    public void testApplyTrue() {
        Program program = MediaTestDataBuilder.program().withGeoRestrictions().build();
        assertThat(new HasGeoRestrictionConstraint().test(program)).isTrue();
    }

    @Test
    public void testApplyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasGeoRestrictionConstraint().test(program)).isFalse();
    }

    @Test
    public void testGetESPath() {
        assertThat(new HasGeoRestrictionConstraint().getESPath()).isEqualTo("regions");
    }
}
