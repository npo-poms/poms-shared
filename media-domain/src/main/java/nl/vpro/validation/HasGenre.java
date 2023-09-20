package nl.vpro.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({  TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = HasGenreValidator.class)
@Documented
public @interface HasGenre {

    String message() default "{nl.vpro.constraints.hasgenre}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
