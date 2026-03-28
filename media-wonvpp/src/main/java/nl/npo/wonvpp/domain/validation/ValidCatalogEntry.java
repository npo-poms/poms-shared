package nl.npo.wonvpp.domain.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CatalogEntryValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ValidCatalogEntry {
    /** Message used when this composed constraint is reported as a single violation. */
    String message() default "{nl.vpro.constraints.catalogEntry}";

    /** Allows the specification of validation groups to which this constraint belongs. */
    Class<?>[] groups() default {};

    /** Payload for clients of the Jakarta Bean Validation API to assign custom payload objects to a constraint. */
    Class<? extends Payload>[] payload() default {};
}
