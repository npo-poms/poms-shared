/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.api.AbstractTextMatcher;
import nl.vpro.domain.api.MatchType;

/**
 * @author rico
 * @since 3.1
 */
public class TextMatcherValidator implements ConstraintValidator<ValidTextMatcher, AbstractTextMatcher> {


    @Override
    public boolean isValid(AbstractTextMatcher textMatcher, ConstraintValidatorContext context) {
        if (textMatcher == null) {
            return true;
        }
        final MatchType matchType = textMatcher.getMatchType();
        MatchType.Validation validation = matchType.valid(textMatcher.getValue());
        if (! validation.isValid()) {
            context.disableDefaultConstraintViolation();
            context
                .buildConstraintViolationWithTemplate(validation.getMessage())
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }
        return true;
    }


}
