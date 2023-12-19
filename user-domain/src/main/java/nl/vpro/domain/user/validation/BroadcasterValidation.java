package nl.vpro.domain.user.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Michiel Meeuwissen
 * @since 7.10
 */

@Target({TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = BroadcasterValidator.class)
@Documented
public @interface BroadcasterValidation {

    String message() default  "{nl.vpro.constraints.broadcaster}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
