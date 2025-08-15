/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.TextualObjects;

/**
 * @since 7.7
 * @see HasTitle
 */
public class HasTitleValidator implements ConstraintValidator<HasTitle, TextualObjectUpdate<?, ?, ?>> {


    HasTitle annotation;

    @Override
    public void initialize(HasTitle constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }


    @Override
    public boolean isValid(TextualObjectUpdate<?, ?, ?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !StringUtils.isEmpty(TextualObjects.get(value.getTitles(), annotation.type()));
    }
}
