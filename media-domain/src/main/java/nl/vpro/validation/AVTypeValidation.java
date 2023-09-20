package nl.vpro.validation;

import java.lang.annotation.*;

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
@Constraint(validatedBy = AVTypeValidator.class)
@Documented
public @interface AVTypeValidation {

    String message() default  "{nl.vpro.constraints.avtype}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
