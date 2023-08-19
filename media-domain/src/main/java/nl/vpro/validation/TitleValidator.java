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
 */
public class TitleValidator implements ConstraintValidator<Title, TextualObjectUpdate<?, ?, ?>> {


    Title annotation;

	@Override
    public void initialize(Title constraintAnnotation) {
        this.annotation = constraintAnnotation;
	}


    @Override
    public boolean isValid(TextualObjectUpdate<?, ?, ?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (StringUtils.isEmpty(TextualObjects.get(value.getTitles(), annotation.type()))) {
            return false;
        }
        return true;
    }
}
