/*
 * Copyright (C) 2023 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Not all {@link nl.vpro.domain.media.AVType av-types} are valid for all {@link nl.vpro.domain.media.MediaObject mediaobjects}.
 *
 * @author Michiel Meeuwissen
 * @since 7.8
 * @see nl.vpro.domain.media.AVType#test(Object)
 */

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AVTypeValidator.class)
@Documented
public @interface AVTypeValidation {

    String message() default  "{nl.vpro.constraints.avtype}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}