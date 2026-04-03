package nl.vpro.domain.media;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Composed constraint for media IDs (MID).
 * Applies a size and pattern constraint and exposes standard constraint attributes
 * so it behaves like any other Jakarta Bean Validation constraint.
 */
@Documented
@Constraint(validatedBy = {})
@Size(max = 255, min = 4)
@Pattern(
    regexp = "^[a-zA-Z0-9][ .a-zA-Z0-9_-]*$",
    flags = { Pattern.Flag.CASE_INSENSITIVE }
)
@ReportAsSingleViolation
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ValidMid {

    /** Message used when this composed constraint is reported as a single violation. */
    String message() default "{nl.vpro.constraints.mid}";

    /** Allows the specification of validation groups to which this constraint belongs. */
    Class<?>[] groups() default {};

    /** Payload for clients of the Jakarta Bean Validation API to assign custom payload objects to a constraint. */
    Class<? extends Payload>[] payload() default {};

}
