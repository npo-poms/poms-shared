/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @author rico
 * @since 3.1
 */
@Documented
@Constraint(validatedBy = TextMatcherValidator.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTextMatcher {

    String message() default "{ValidTextMatcher}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
