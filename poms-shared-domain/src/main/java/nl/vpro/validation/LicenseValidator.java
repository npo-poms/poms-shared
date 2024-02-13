package nl.vpro.validation;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.support.License;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class LicenseValidator implements ConstraintValidator<LicenseId, String> {

    @Override
    public boolean isValid(String id, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(License.values()).anyMatch(l -> l.getId().equals(id));
    }
}

