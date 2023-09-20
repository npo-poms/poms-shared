/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author rico
 * @since 3.0
 */

@Documented
@Constraint(validatedBy = BroadcasterValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBroadcaster {
    String message() default "{ValidBroadcaster}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
