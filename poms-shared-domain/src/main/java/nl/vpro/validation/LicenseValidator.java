package nl.vpro.validation;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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

