/*
 * Copyright (C) 2025 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SocialRefValidator implements ConstraintValidator<SocialRef , nl.vpro.domain.media.SocialRef> {


    public static final Pattern HASH_PATTERN = Pattern.compile("^#\\w{1,279}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern TWITTER_ACCOUNT_PATTERN = Pattern.compile("^@\\\\w{1,50}", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(nl.vpro.domain.media.SocialRef socialRef, ConstraintValidatorContext context) {
        if (socialRef == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();

        String validatedValue = socialRef.getValue();
        if(validatedValue == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("value")
                .addConstraintViolation();
            return false;
        }
        nl.vpro.domain.media.SocialRef.Type type = socialRef.getType();

        if(type == null) {
            context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.NotNull}")
                .addPropertyNode("type")
                .addConstraintViolation();
            return false;
        }

        if (type == nl.vpro.domain.media.SocialRef.Type.HASHTAG) {
            if (!HASH_PATTERN.matcher(validatedValue).matches()) {
                context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.socialRefs.Pattern}")
                    .addPropertyNode("value")
                    .addConstraintViolation();
                return false;
            }
        }
        if (type == nl.vpro.domain.media.SocialRef.Type.ACCOUNT) {
            if (!TWITTER_ACCOUNT_PATTERN.matcher(validatedValue).matches()) {
                context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.socialRefs.Pattern}")
                    .addPropertyNode("value")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }

}
