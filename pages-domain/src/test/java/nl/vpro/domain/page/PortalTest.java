/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class PortalTest {

    private Portal target;

    @BeforeEach
    public void setUp() {
        target = new Portal();
    }

    @Test
    public void testGetUrl() {
        target.setUrl("http://tegenlicht.vpro.nl/");
        assertThat(target.getUrl()).isEqualTo("http://tegenlicht.vpro.nl");
    }

    @Test
    public void testGetDisplayName() {
        target.setDisplayName("Wetenschap24");
        assertThat(target.getDisplayName()).isEqualTo("Wetenschap24");
    }

    @Test
    public void testXmlBinding() {
        target.setUrl("http://tegenlicht.vpro.nl/");
        target.setDisplayName("Wetenschap24");
        target.setSection(new Section("/noorderlicht", "Noorderlicht"));
        JAXBTestUtil.roundTripAndSimilar(target, """
            <local:portal url="http://tegenlicht.vpro.nl" xmlns:pages="urn:vpro:pages:2013" xmlns:local="uri:local">
                <pages:name>Wetenschap24</pages:name>
                <pages:section path="/noorderlicht">Noorderlicht</pages:section>
            </local:portal>""");
    }
}
