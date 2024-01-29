package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Segments must have either a midRef of a parent.
 * @author Michiel Meeuwissen
 * @since 2.3.1
 */

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SegmentValidator.class)
@Documented
public @interface SegmentValidation {

    String message() default "{nl.vpro.constraints.segment}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
