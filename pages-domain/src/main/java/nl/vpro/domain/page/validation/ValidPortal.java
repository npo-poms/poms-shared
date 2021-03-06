/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

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
