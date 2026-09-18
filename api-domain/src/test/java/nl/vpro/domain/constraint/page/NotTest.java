/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlors
 */
package nl.vpro.domain.constraint.page;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
class NotTest {

    @Test
    void getConstraints() {
        Not in = new Not(new PageTypeConstraint(PageType.ARTICLE));
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:local="uri:local">
                    <page:type>ARTICLE</page:type>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(PageTypeConstraint.class);
    }

    @Test
    void applyWhenTrue() {
        assertThat(new Not(PageConstraints.alwaysFalse()).test(new Page(PageType.ARTICLE))).isTrue();
    }


    @Test
    void applyWhenFalse() {
        assertThat(new Not(PageConstraints.alwaysTrue()).test(new Page(PageType.ARTICLE))).isFalse();
    }
}
