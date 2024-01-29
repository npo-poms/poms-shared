/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.classification.*;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Slf4j
public class GenreValidator implements ConstraintValidator<ValidGenre, String> {

    ValidGenre annotation;

    @Override
    public void initialize(ValidGenre constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        ClassificationService classificationService = ClassificationServiceLocator.getInstance();
        if (classificationService == null) {
            log.warn("No classification service found");
        } else {
            if (classificationService.hasTerm(value)) {
                Term term = classificationService.getTerm(value);
                return term.getTermId().split("\\.").length >= annotation.minItems();
            } else {
                return false;
            }
        }
        return true;
    }

}
