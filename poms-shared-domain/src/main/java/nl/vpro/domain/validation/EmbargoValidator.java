package nl.vpro.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.Embargo;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class EmbargoValidator implements ConstraintValidator<ValidEmbargo, Embargo> {


    @Override
    public boolean isValid(Embargo value, ConstraintValidatorContext constraintValidatorContext) {
        return value.getPublishStartInstant() == null
            || value.getPublishStopInstant() == null
            || (!value.getPublishStartInstant().isAfter(value.getPublishStopInstant()));

    }
}
