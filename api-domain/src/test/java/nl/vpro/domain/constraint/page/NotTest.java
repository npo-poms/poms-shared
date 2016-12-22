/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlors
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class NotTest {

    @Test
    public void testGetConstraints() throws Exception {
        Not in = new Not(new PageTypeConstraint("article"));
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">\n" +
                "    <page:type>article</page:type>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(PageTypeConstraint.class);
    }

    @Test
    public void testApplyWhenTrue() {
        assertThat(new Not(PageConstraints.alwaysFalse()).test(new Page(PageType.ARTICLE))).isTrue();
    }


    @Test
    public void testApplyWhenFalse() {
        assertThat(new Not(PageConstraints.alwaysTrue()).test(new Page(PageType.ARTICLE))).isFalse();
    }
}
