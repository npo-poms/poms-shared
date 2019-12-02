package nl.vpro.domain.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmbargoValidator.class)
@Documented
public @interface ValidEmbargo {
    String message() default "{nl.vpro.constraints.publishable}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

