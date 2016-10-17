/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.ClassificationServiceLocator;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class GenreValidator implements ConstraintValidator<ValidGenre, Iterable<String>> {
    private static final Logger LOG = LoggerFactory.getLogger(GenreValidator.class);


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
            LOG.warn("No classification service found");
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
