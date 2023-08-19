/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
