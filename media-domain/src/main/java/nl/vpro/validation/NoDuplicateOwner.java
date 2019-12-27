package nl.vpro.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;

import nl.vpro.domain.media.support.Ownable;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Ensures that a collection of {@link Ownable} contains no two entries with the same {@link Ownable#getOwner()}
 *
 * @author Michiel Meeuwissen
 * @since 5.512
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NoDuplicateOwnerValidator.class)
@Documented
public @interface NoDuplicateOwner {
}
