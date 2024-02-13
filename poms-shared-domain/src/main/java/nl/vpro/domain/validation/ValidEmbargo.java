package nl.vpro.domain.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import nl.vpro.domain.Embargo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks whether the embargo settings {@link Embargo#getPublishStartInstant()} and {@link Embargo#getPublishStopInstant()} are consistent. I.e. the latter one is after the first one.
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

