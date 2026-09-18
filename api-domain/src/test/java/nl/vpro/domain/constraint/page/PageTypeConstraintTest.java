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
class PageTypeConstraintTest {

    @Test
    void getValue() {
        PageTypeConstraint in = new PageTypeConstraint(PageType.HOME);
        PageTypeConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageTypeConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">HOME</local:pageTypeConstraint>");
        assertThat(out.getValue()).isEqualTo("HOME");
    }

    @Test
    void getESPath() {
        assertThat(new PageTypeConstraint().getESPath()).isEqualTo("type");
    }

    @Test
    void applyWhenTrue() {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint(PageType.ARTICLE).test(article)).isTrue();
    }

    @Test
    void applyWhenFalse() {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint(PageType.SERIES).test(article)).isFalse();
    }
}
