/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Program;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
class MediaTypeConstraintTest {

    @Test
    void getStringValue() {
        MediaTypeConstraint in = new MediaTypeConstraint(MediaType.BROADCAST);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaTypeConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">BROADCAST</local:mediaTypeConstraint>");
    }

    @Test
    void getESPath() {
        assertThat(new MediaTypeConstraint().getESPath()).isEqualTo("type");
    }

    @Test
    void applyWhenTrue() {
        Program program = MediaTestDataBuilder.program().mid("mid_1234").withType().build();
        assertThat(new MediaTypeConstraint(MediaType.BROADCAST).test(program)).isTrue();
        assertThat(new MediaTypeConstraint(MediaType.BROADCAST).testWithReason(program)
            .getDescription(Locales.DUTCH)).isEqualTo("mid_1234 is een uitzending");
    }

    @Test
    void applyWhenFalse() {
        Program program = MediaTestDataBuilder.program().mid("mid_1234").withType().build();
        assertThat(new MediaTypeConstraint(MediaType.SEGMENT).test(program)).isFalse();
        assertThat(new MediaTypeConstraint(MediaType.SEGMENT).testWithReason(program)
            .getDescription(Locales.DUTCH)).isEqualTo("mid_1234 is geen segment");
    }
}
