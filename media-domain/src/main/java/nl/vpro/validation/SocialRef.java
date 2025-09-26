/*
 * Copyright (C) 2025 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SocialRefValidator.class)
@Documented
public @interface SocialRef {
    String message() default "{nl.vpro.constraints.SocialRef}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
