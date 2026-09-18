/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jeroen van Vianen
 * @since 4.8
 */
class ContentRatingsConstraintTest {

    @Test
    void getStringValue() {
        ContentRatingConstraint in = new ContentRatingConstraint(ContentRating.ANGST);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:contentRatingConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:local=\"uri:local\">ANGST</local:contentRatingConstraint>\n");
    }

    @Test
    void getESPath() {
        assertThat(new ContentRatingConstraint().getESPath()).isEqualTo("contentRatings");
    }

    @Test
    void contentRatingAngst()  {
        Program program = MediaTestDataBuilder.program().withContentRating().build();
        assertThat(new ContentRatingConstraint(ContentRating.ANGST).test(program)).isTrue();
    }

    @Test
    void contentRatingDiscriminatieFails() {
        Program program = MediaTestDataBuilder.program().withContentRating().build();
        assertThat(new ContentRatingConstraint(ContentRating.DISCRIMINATIE).test(program)).isFalse();
    }
}
