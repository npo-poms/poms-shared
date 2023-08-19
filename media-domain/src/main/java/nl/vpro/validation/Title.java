package nl.vpro.validation;


import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import nl.vpro.domain.media.support.TextualType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @since 7.7
 */
@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = TitleValidator.class)
@Documented
@Repeatable(Title.List.class)
public @interface Title {
    String message() default "{nl.vpro.constraints.maintitle}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    TextualType type() default TextualType.SUB;


	/**
	 * Defines several {@code @URI} constraints on the same element.
	 *
	 * @see Title
	 */
@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Title[] value();
	}
}
