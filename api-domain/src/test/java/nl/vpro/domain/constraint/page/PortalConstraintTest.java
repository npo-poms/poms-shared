/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.*;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class PortalConstraintTest {

    @Test
    public void testGetValue() {
        PortalConstraint in = new PortalConstraint("VPRONL");
        PortalConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:portalConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">VPRONL</local:portalConstraint>\n");
        assertThat(out.getValue()).isEqualTo("VPRONL");
    }

    @Test
    public void testGetESPath() {
        assertThat(new PortalConstraint().getESPath()).isEqualTo("portal.id");
    }

    @Test
    public void testApplyWhenTrue() {
        Page article = PageBuilder.page(PageType.ARTICLE).portal(new Portal("VPRONL", "http://www.vpro.nl", "VproNL")).build();
        assertThat(new PortalConstraint("VPRONL").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Page article = PageBuilder.page(PageType.ARTICLE).portal(new Portal("VPRONL", "http://www.vpro.nl", "VproNL")).build();
        assertThat(new PortalConstraint("CULTURA24").test(article)).isFalse();
    }
}
