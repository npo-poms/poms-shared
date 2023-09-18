package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = GenreValidator.class)
@Documented
public @interface GenreValidation {

    String message() default  "{nl.vpro.constraints.genre}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
