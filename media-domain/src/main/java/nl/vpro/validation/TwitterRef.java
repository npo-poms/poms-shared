/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = TwitterRefValidator.class)
@Documented
public @interface TwitterRef {
    String message() default "{nl.vpro.constraints.TwitterRef}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
