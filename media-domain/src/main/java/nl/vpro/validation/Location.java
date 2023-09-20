/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author Michiel Meeuwissen
 * @since 3.7
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = LocationValidator.class)
@Documented
public @interface Location {
    String message() default "{nl.vpro.constraints.Location}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
