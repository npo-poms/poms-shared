package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = LicenseValidator.class)
@Documented
public @interface LicenseId {
    String message() default "{nl.vpro.constraints.licenseId}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

