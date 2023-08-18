/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.TextualObjectUpdate;

/**
 * @since 7.7
 */
public class MainTitleValidator implements ConstraintValidator<MainTitle, TextualObjectUpdate<?, ?, ?>> {

    @Override
    public boolean isValid(TextualObjectUpdate<?, ?, ?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (StringUtils.isEmpty(value.getMainTitle())) {
            return false;
        }
        return false;
    }
}
