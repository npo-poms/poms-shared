/*
 * Copyright (C) 2020 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.thesaurus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.gtaa.Scheme;

/**
 * This can validate whether a String is a valid {@link Scheme}. See {@link Scheme#fromString(String)}
 */
public class GtaaSchemeValidator implements ConstraintValidator<ValidGtaaScheme, String> {

    @Override
    public void initialize(ValidGtaaScheme crid) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Scheme.fromString(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
