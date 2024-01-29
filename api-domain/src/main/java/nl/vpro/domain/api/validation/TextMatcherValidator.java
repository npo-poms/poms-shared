/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
