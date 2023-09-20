/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.Genre;

public class GenreValidator implements ConstraintValidator<GenreValidation, Genre> {

    @Override
    public boolean isValid(Genre value, ConstraintValidatorContext constraintValidatorContext) {
        return ClassificationServiceLocator.getInstance().hasTerm(value.getTermId());
    }
}
