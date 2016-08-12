package nl.vpro.domain.page.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */

@Documented
@Constraint(validatedBy = GenreValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGenre {
    String message() default "Not a valid genre";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

