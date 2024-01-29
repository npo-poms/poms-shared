/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ImageURIValidator.class)
@Documented
public @interface ImageURI {
    String message() default "{nl.vpro.constraints.imageuri}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
