package nl.vpro.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import nl.vpro.domain.media.MediaIdentifiable;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks whether a {@link MediaIdentifiable} as at least one genre if that is required according to {@link MediaIdentifiable#getMediaType() media type}.{@link nl.vpro.domain.media.MediaType#requiresGenre() #requiresGenre()
 * @since 7.8
 */
@Target({  TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = HasGenreValidator.class)
@Documented
public @interface HasGenre {

    String message() default "{nl.vpro.constraints.hasgenre}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
