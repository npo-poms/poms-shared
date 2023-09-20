/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TwitterRefValidator implements ConstraintValidator<TwitterRef , nl.vpro.domain.media.TwitterRef> {

    public static final Pattern PATTERN = Pattern.compile("^@\\w{1,50}|#\\w{1,279}$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(nl.vpro.domain.media.TwitterRef twitterRef, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        String validatedValue = twitterRef.getValue();
        if(validatedValue == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }


        final Matcher matcher = PATTERN.matcher(validatedValue);
        if(!matcher.find()) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.twitterRefs.Pattern}")
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }

        if(twitterRef.getType() == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("type")
                .addConstraintViolation();
            return false;
        }

        return true;
    }

}
