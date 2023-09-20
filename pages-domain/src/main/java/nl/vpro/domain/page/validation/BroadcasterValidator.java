/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.ServiceLocator;


/**
 * @author rico
 * @since 3.0
 */
@Slf4j
public class BroadcasterValidator implements ConstraintValidator<ValidBroadcaster, Iterable<String>> {
    @Override
    public void initialize(ValidBroadcaster constraintAnnotation) {

    }

    @Override
    public boolean isValid(Iterable<String> values, ConstraintValidatorContext context) {
        BroadcasterService broadcasterService = ServiceLocator.getBroadcasterService();

        if (values != null && broadcasterService != null) {
            if (broadcasterService.findAll().size() > 0) {
                for (String value : values) {
                    if (broadcasterService.find(value) == null) {
                        return false;
                    }
                }
            } else {
                log.warn("No values found in {}", broadcasterService);
            }

        }
        return true;
    }

}
