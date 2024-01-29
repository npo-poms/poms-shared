/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user.validation;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.user.*;

/**
 * @since 7.10
 */
@Slf4j
public class BroadcasterValidator implements ConstraintValidator<BroadcasterValidation, Object> {

    private static boolean warned = false;

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {

        final BroadcasterService broadcasterService =  ServiceLocator.getBroadcasterService();
        if (broadcasterService == null || broadcasterService.findAll().isEmpty()) {
            if (! warned) {
                log.warn("No broadcaster service found, so no validation");
            }
            warned = true;
            return true;
        }
        warned = false;
        if (value instanceof Iterable i) {
            for (Object o : i) {
                if (! isValid(o, constraintValidatorContext)) {
                    return false;
                }
            }
            return true;
        }
        if (value instanceof Broadcaster b) {
            value  = b.getId();
        }
        if (value instanceof CharSequence cs) {
            return broadcasterService.find(cs.toString()) != null;
        }
        throw new IllegalArgumentException("Cannot validate " + value);
    }
}
