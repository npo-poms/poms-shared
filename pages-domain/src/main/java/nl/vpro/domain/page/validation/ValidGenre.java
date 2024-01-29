package nl.vpro.domain.page.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */

@Documented
@Constraint(validatedBy = GenreValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGenre {
    String message() default "Not a valid genre";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minItems() default 4;
}

