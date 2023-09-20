/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.domain.page.*;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 * @since 4.3
 */
public class SectionConstraintTest {
    @Test
    public void testGetValue() {
        SectionConstraint in = new SectionConstraint("cinema");
        SectionConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:sectionConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">cinema</local:sectionConstraint>\n");
        assertThat(out.getValue()).isEqualTo("cinema");
    }

    @Test
    public void testGetESPath() {
        assertThat(new SectionConstraint().getESPath()).isEqualTo("portal.section.path");
    }

    @Test
    public void testApplyWhenTrue() {
        final Portal portal = new Portal("VPRONL", "http://www.vpro.nl", "VproNL");
        final Section section = new Section("cinema", "Cinema NL");
        portal.setSection(section);
        Page article = PageBuilder.page(PageType.ARTICLE).portal(portal).build();
        assertThat(new SectionConstraint("cinema").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        final Portal portal = new Portal("VPRONL", "http://www.vpro.nl", "VproNL");
        final Section section = new Section("cinema", "Cinema NL");
        portal.setSection(section);
        Page article = PageBuilder.page(PageType.ARTICLE).portal(portal).build();
        assertThat(new SectionConstraint("tegenlicht").test(article)).isFalse();
    }
}
