package nl.vpro.api.rs.v3.thesaurus;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@Constraint(validatedBy = GtaaSchemeValidator.class)
@Documented
public @interface ValidGtaaScheme {


    String message() default "{nl.vpro.constraints.gtaascheme}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
