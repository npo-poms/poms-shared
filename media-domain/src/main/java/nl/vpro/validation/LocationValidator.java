package nl.vpro.validation;

import java.net.URISyntaxException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Michiel Meeuwissen
 * @since 3.7
 */
public class LocationValidator implements ConstraintValidator<Location, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return true;
            }

        try {
            java.net.URI uri = new java.net.URI(value);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }
            return true;
        } catch(URISyntaxException e) {
            return false;
        }
    }
}
