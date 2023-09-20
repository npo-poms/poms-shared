/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.media.*;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jeroen van Vianen
 * @since 4.9
 */
public class AgeRatingConstraintTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testGetStringValue() {
        AgeRatingConstraint in = new AgeRatingConstraint(AgeRating._6);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:ageRatingConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:local=\"uri:local\">6</local:ageRatingConstraint>\n");
    }

    @Test
    public void testGetESPath() {
        assertThat(new AgeRatingConstraint().getESPath()).isEqualTo("ageRating");
    }

    @Test
    public void testAgeRating12() {
        Program program = MediaTestDataBuilder.program().withAgeRating().build();
        assertThat(new AgeRatingConstraint(AgeRating._12).test(program)).isTrue();
    }

    @Test
    public void testAgeRating6Fails()  {
        Constraint<MediaObject> _6 = new AgeRatingConstraint(AgeRating._6);
        Program program = MediaTestDataBuilder.program().mid("mid_1235").withAgeRating().build();
        assertThat(_6.test(program)).isFalse();
        assertThat(_6.testWithReason(program).getDescription(Locale.US)).isEqualTo("The age rating of 'mid_1235' is not 6");
        assertThat(_6.testWithReason(program).getReason()).isEqualTo("AgeRatingConstraint/ageRating/6");
    }
 }
