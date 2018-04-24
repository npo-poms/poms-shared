package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = LanguageValidator.class)
@Documented
public @interface Language {

    String message() default "{nl.vpro.constraints.language}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
