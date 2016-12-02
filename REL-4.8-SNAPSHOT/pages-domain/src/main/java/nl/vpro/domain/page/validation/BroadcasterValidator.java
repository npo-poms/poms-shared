/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.ServiceLocator;


/**
 * @author rico
 * @since 3.0
 */
public class BroadcasterValidator implements ConstraintValidator<ValidBroadcaster, Iterable<String>> {
    @Override
    public void initialize(ValidBroadcaster constraintAnnotation) {

    }

    @Override
    public boolean isValid(Iterable<String> values, ConstraintValidatorContext context) {
        BroadcasterService broadcasterService = ServiceLocator.getBroadcasterService();

        if (values != null && broadcasterService != null) {
            for(String value : values) {
                if (broadcasterService.find(value) == null) {
                    return false;
                }
            }
        }
        return true;
    }

}
