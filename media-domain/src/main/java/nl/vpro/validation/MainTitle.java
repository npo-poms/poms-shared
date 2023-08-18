package nl.vpro.validation;


import java.lang.annotation.*;

import javax.validation.Constraint;

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
}
