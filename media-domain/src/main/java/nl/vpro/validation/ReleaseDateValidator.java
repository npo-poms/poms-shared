package nl.vpro.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, String>  {
    static final Pattern YEAR_ONLY = Pattern.compile("[1-9]\\d{3}");

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext){
        return date == null || YEAR_ONLY.matcher(date).matches();
    }
}
