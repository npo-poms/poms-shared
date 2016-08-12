package nl.vpro.domain.page.validation;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = RelationValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRelation {

    String message() default "{ValidRelation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
