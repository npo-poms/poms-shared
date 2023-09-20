/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlors
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class OrTest {

    @Test
    public void testGetConstraints() {
        Or in = new Or(new BroadcasterConstraint("VPRO"), new PortalConstraint("http://www.vpro.nl"));
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:local="uri:local">
                    <page:broadcaster>VPRO</page:broadcaster>
                    <page:portal>http://www.vpro.nl</page:portal>
                </local:or>""");

        assertThat(out.getConstraints()).hasSize(2);
    }

    @Test
    public void testApplyWhenEmpty() {
        Or constraint = new Or();
        assertThat(constraint.test(null)).isFalse();
    }

    @Test
    public void testApplyWhenFalse() {
        Or constraint = new Or(
            PageConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isFalse();
    }

    @Test
    public void testApplyWhenTrue() {
        Or constraint = new Or(
            PageConstraints.alwaysFalse(),
            PageConstraints.alwaysTrue()
        );
        assertThat(constraint.test(null)).isTrue();
    }
}
