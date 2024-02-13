/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */

@Documented
@Constraint(validatedBy = PortalValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPortal  {
    String message() default "{ValidPortal}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
