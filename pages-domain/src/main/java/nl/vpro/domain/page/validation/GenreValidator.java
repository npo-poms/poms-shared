/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.ClassificationServiceLocator;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Slf4j
public class GenreValidator implements ConstraintValidator<ValidGenre, Iterable<String>> {


    @Override
    public void initialize(ValidGenre constraintAnnotation) {

    }

    @Override
    public boolean isValid(Iterable<String> values, ConstraintValidatorContext context) {
        if (values == null) {
            return true;
        }
        ClassificationService classificationService = ClassificationServiceLocator.getInstance();
        if (classificationService == null) {
            log.warn("No classification service found");
        } else {
            for (String value : values) {
                if (! classificationService.hasTerm(value)) {
                    return false;
                }
            }
        }
        return true;
    }

}
