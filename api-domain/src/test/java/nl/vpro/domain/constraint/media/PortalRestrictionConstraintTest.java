/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.user.Portal;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
public class PortalRestrictionConstraintTest {

    @Test
    public void testGetStringValue() {
        PortalRestrictionConstraint in = new PortalRestrictionConstraint(new Portal("VPRO", "VPRO.nl"));
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:portalRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">VPRO</local:portalRestrictionConstraint>\n");
    }

    @Test
    public void testGetESPath() {
        assertThat(new PortalRestrictionConstraint().getESPath()).isEqualTo("exclusives");
    }

    @Test
    public void testApplyWhenTrue() {
        Program program = MediaTestDataBuilder.program().withPortalRestrictions().build();
        assertThat(new PortalRestrictionConstraint(new Portal("STERREN24", "Sterretjes")).test(program)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Program program = MediaTestDataBuilder.program().withPortalRestrictions().build();
        assertThat(new PortalRestrictionConstraint(new Portal("3voor12", "drie voor twaalf")).test(program)).isFalse();
    }
}
