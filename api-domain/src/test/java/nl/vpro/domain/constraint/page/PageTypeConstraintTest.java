/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class PageTypeConstraintTest {

    @Test
    public void testGetValue() {
        PageTypeConstraint in = new PageTypeConstraint(PageType.HOME);
        PageTypeConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageTypeConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">HOME</local:pageTypeConstraint>");
        assertThat(out.getValue()).isEqualTo("HOME");
    }

    @Test
    public void testGetESPath() {
        assertThat(new PageTypeConstraint().getESPath()).isEqualTo("type");
    }

    @Test
    public void testApplyWhenTrue() {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint(PageType.ARTICLE).test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint(PageType.SERIES).test(article)).isFalse();
    }
}
