package nl.vpro.validation;


import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @since 7.7
 */
@Target({METHOD, FIELD, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MainTitleValidator.class)
@Documented
public @interface MainTitle {
    String message() default "{nl.vpro.constraints.maintitle}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
