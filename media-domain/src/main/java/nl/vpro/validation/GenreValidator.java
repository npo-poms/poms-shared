/*
 * Copyright (C) 2011 All rights reserved
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
