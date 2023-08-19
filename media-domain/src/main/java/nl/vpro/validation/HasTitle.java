package nl.vpro.validation;


import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.media.support.TextualType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks if the validated {@link TextualObjectUpdate} has a title of the given type.
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
	 * The type of the title to require. Defaults to {@link TextualType#SUB}
	 */
    TextualType type() default TextualType.SUB;


	/**
	 * Defines several {@code @URI} constraints on the same element.
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
