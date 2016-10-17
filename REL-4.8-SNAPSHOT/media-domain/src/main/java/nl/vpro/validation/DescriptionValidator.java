/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DescriptionValidator implements ConstraintValidator<Description, nl.vpro.domain.media.support.Description> {

    @Override
    public void initialize(Description description) {
    }

    @Override
    public boolean isValid(nl.vpro.domain.media.support.Description description, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        switch(description.getType()) {
            case KICKER:
/*
                disabled see comment on MSE-2671
                final String value = description.getDescription();
                if(value != null && value.length() > 140) {
                    context.buildConstraintViolationWithTemplate("{nl.vpro.constraints.Description.KICKER.max}")
                        .addPropertyNode("description")
                        .addConstraintViolation();
                    return false;
                }
*/
        }

        return true;
    }
}
