/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class BroadcasterConstraintTest {

    @Test
    public void testGetValue() {
        BroadcasterConstraint in = new BroadcasterConstraint("VPRO");
        BroadcasterConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:broadcasterConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">VPRO</local:broadcasterConstraint>");
        assertThat(out.getValue()).isEqualTo("VPRO");
    }

    @Test
    public void testGetESPath() {
        assertThat(new BroadcasterConstraint().getESPath()).isEqualTo("broadcasters.id");
    }

    @Test
    public void testApplyWhenTrue() {
        Page article = PageBuilder.page(PageType.ARTICLE).broadcasters(new Broadcaster("BNN", "BNN")).build();
        assertThat(new BroadcasterConstraint("BNN").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Page article = PageBuilder.page(PageType.ARTICLE).broadcasters(new Broadcaster("BNN", "BNN")).build();
        assertThat(new BroadcasterConstraint("Bnn").test(article)).isFalse();
    }
}
