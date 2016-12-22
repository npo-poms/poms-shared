/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class PageTypeConstraintTest {

    @Test
    public void testGetValue() throws Exception {
        PageTypeConstraint in = new PageTypeConstraint("VPRO");
        PageTypeConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageTypeConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">VPRO</local:pageTypeConstraint>");
        assertThat(out.getValue()).isEqualTo("VPRO");
    }

    @Test
    public void testGetESPath() throws Exception {
        assertThat(new PageTypeConstraint().getESPath()).isEqualTo("type");
    }

    @Test
    public void testApplyWhenTrue() throws Exception {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint("Article").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() throws Exception {
        Page article = PageBuilder.page(PageType.ARTICLE).build();
        assertThat(new PageTypeConstraint("news").test(article)).isFalse();
    }
}
