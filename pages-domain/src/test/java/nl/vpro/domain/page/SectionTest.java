/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import java.util.Set;

import javax.validation.*;

import org.junit.jupiter.api.*;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class SectionTest {

    private static Validator validator;
    private Section target;

    @BeforeAll
    public static void before() {
        ValidatorFactory config = Validation.buildDefaultValidatorFactory();
        validator = config.getValidator();
    }

    @BeforeEach
    public void setUp() {
        target = new Section();
    }

    @Test
    public void testGetPath() {
        target.setPath("/tegenlicht");
        assertThat(target.getPath()).isEqualTo("/tegenlicht");
    }

    @Test
    public void testId() {
        Portal portal = new Portal("VPRONL", "http://www.vpro.nl", "VPRO");
        target.setPortal(portal);
        target.setPath("/tegenlicht");
        assertThat(target.getId()).isEqualTo("VPRONL./tegenlicht");
    }

    @Test
    public void testXmlBinding() {
        target.setPath("/tegenlicht");
        target.setDisplayName("Tegenlicht");
        Portal portal = new Portal();
        portal.setId("vpronl");
        portal.setSection(target);
        Portal result = JAXBTestUtil.roundTripAndSimilar(portal, """
            <local:portal id="vpronl" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                <pages:section path="/tegenlicht">Tegenlicht</pages:section>
            </local:portal>""");
        assertThat(result.getSection()).isNotNull();
        assertThat(result.getSection().getPortal()).isSameAs(result);
        assertThat(result.getSection().getDisplayName()).isEqualTo("Tegenlicht");

    }

    @Test
    public void testJsonBinding() {
        target.setPath("/tegenlicht");
        target.setDisplayName("Tegenlicht");
        Portal portal = new Portal();
        portal.setId("vpronl");
        portal.setSection(target);
        Portal result = Jackson2TestUtil.roundTripAndSimilar(portal, """
            {
              "id" : "vpronl",
              "section" : {
                "path" : "/tegenlicht",
                "id" : "vpronl./tegenlicht",
                "value" : "Tegenlicht"
              }
            }""");
        assertThat(result.getSection()).isNotNull();
        assertThat(result.getSection().getPortal()).isSameAs(result);
        assertThat(result.getSection().getDisplayName()).isEqualTo("Tegenlicht");

    }

    @Test
    public void testSectionPath() {
        Portal portal = new Portal("VPRONL", "http://www.vpro.nl", "VPRO");
        target.setPortal(portal);
        target.setPath("/tegenlicht");
        target.setDisplayName("Tegenlicht");
        System.out.println(target.getId());
        Set<ConstraintViolation<Section>> constraintViolations = validator.validate(target);
        assertThat(constraintViolations.size()).isZero();
    }

    @Test
    public void testInvalidSectionPath() {
        Portal portal = new Portal("VPRONL", "http://www.vpro.nl", "VPRO");
        target.setPortal(portal);
        target.setPath("http://tegenlicht.vpro.nl");
        target.setDisplayName("Tegenlicht");
        Set<ConstraintViolation<Section>> constraintViolations = validator.validate(target);
        assertThat(constraintViolations.size()).isNotEqualTo(0);

    }
}
