/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import org.junit.jupiter.api.Test;

import nl.vpro.validation.ImageURIValidator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class ImageURIValidatorTest {

    ImageURIValidator validator = new ImageURIValidator();

    /**
     * Legacy. Once there was a bug which distributed uri's like this
     */
    @Test
    public void testIsValidWithDot() {
        String uri = "urn:vpro.image:12345";
        assertThat(validator.isValid(uri, null)).isTrue();
    }

    @Test
    public void testIsValidWithColon() {
        String uri = "urn:vpro:image:12345";
        assertThat(validator.isValid(uri, null)).isTrue();
    }

    @Test
    public void testIsValidWhenInvalid() {
        String uri = "urn:vpro:images:12345";
        assertThat(validator.isValid(uri, null)).isFalse();
    }
}
