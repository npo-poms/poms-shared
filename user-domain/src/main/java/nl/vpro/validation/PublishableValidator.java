/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.MutableEmbargo;

public class PublishableValidator implements ConstraintValidator<EmbargoValidation, MutableEmbargo<?>> {

    @Override
    public boolean isValid(MutableEmbargo<?> value, ConstraintValidatorContext constraintValidatorContext) {
        return value.getPublishStartInstant() == null
            || value.getPublishStopInstant() == null
            || value.getPublishStartInstant().toEpochMilli() <= value.getPublishStopInstant().toEpochMilli();
    }
}
