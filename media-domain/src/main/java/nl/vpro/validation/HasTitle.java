package nl.vpro.validation;


import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.media.support.TextualType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks if the validated {@link TextualObjectUpdate} has at least one of title of the given {@link #type() types}.
 * @since 7.7
 */
@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = HasTitleValidator.class)
@Documented
@Repeatable(HasTitle.List.class)
public @interface HasTitle {
    String message() default "{nl.vpro.constraints.hastitle}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

	/**
	 * The types of the title to require at least one of. Defaults to { {@link TextualType#SUB}, {@link TextualType#MAIN} }
	 */
    TextualType[] type() default {TextualType.SUB, TextualType.MAIN};

	/**
	 * Defines several {@link HasTitle} constraints on the same element.
	 *
	 * @see HasTitle
	 */
	@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		HasTitle[] value();
	}
}
