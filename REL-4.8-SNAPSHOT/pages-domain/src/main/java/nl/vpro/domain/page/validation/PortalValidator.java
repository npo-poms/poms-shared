/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.user.PortalService;
import nl.vpro.domain.user.ServiceLocator;


/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class PortalValidator implements ConstraintValidator<ValidPortal, String> {

    private static final Logger LOG = LoggerFactory.getLogger(PortalValidator.class);

    @Override
    public void initialize(ValidPortal constraintAnnotation) {

    }

    @Override
    public boolean isValid(String portalId, ConstraintValidatorContext context) {
        if (portalId == null) {
            return true;
        }
        PortalService portalService = ServiceLocator.getPortalService();
        if (portalService == null) {
            LOG.warn("No portal service found");
            return false;
        }
        return portalService.find(portalId) != null;
    }

}
