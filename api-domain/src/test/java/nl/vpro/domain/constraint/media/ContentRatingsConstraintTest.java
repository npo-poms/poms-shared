/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jeroen van Vianen
 * @since 4.8
 */
public class ContentRatingsConstraintTest {

    @Test
    public void testGetStringValue() {
        ContentRatingConstraint in = new ContentRatingConstraint(ContentRating.ANGST);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:contentRatingConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:local=\"uri:local\">ANGST</local:contentRatingConstraint>\n");
    }

    @Test
    public void testGetESPath() {
        assertThat(new ContentRatingConstraint().getESPath()).isEqualTo("contentRatings");
    }

    @Test
    public void testContentRatingAngst()  {
        Program program = MediaTestDataBuilder.program().withContentRating().build();
        assertThat(new ContentRatingConstraint(ContentRating.ANGST).test(program)).isTrue();
    }

    @Test
    public void testContentRatingDiscriminatieFails() {
        Program program = MediaTestDataBuilder.program().withContentRating().build();
        assertThat(new ContentRatingConstraint(ContentRating.DISCRIMINATIE).test(program)).isFalse();
    }
}
