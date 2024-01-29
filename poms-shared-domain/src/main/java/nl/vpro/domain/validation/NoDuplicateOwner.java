package nl.vpro.domain.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import nl.vpro.domain.media.support.Ownable;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Ensures that a collection of {@link Ownable} contains no two entries with the same {@link Ownable#getOwner()}
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = NoDuplicateOwnerValidator.class)
@Documented
public @interface NoDuplicateOwner {

    String message() default "The collection contains Ownables with the same owner";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
