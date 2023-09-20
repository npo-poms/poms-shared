/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class BroadcasterConstraintTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testGetStringValue() {
        BroadcasterConstraint in = new BroadcasterConstraint("VPRO");
        BroadcasterConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:broadcasterConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">VPRO</local:broadcasterConstraint>");
        assertThat(out.getValue()).isEqualTo("VPRO");
    }

    @Test
    public void testApplyWhenTrue() {
        Program program = MediaTestDataBuilder.program().withBroadcasters().build();
        assertThat(new BroadcasterConstraint("BNN").test(program)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Program program = MediaTestDataBuilder.program().mid("mid_123").withBroadcasters().build();
        assertThat(new BroadcasterConstraint("Bnn").test(program)).isFalse();
        assertThat(new BroadcasterConstraint("Bnn").testWithReason(program).applies()).isFalse();
        assertThat(new BroadcasterConstraint("Bnn").testWithReason(program).getReason()).isEqualTo("BroadcasterConstraint/broadcasters.id/Bnn");
        assertThat(new BroadcasterConstraint("Bnn").testWithReason(program).getDescription(Locale.US)).isEqualTo("'mid_123' is not of the broadcaster Bnn");
    }

    @Test
    public void testGetESPath() {
        assertThat(new BroadcasterConstraint().getESPath()).isEqualTo("broadcasters.id");
    }
}
