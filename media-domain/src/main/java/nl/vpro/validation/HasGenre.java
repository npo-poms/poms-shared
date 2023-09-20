/*
 * Copyright (C) 2023 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import nl.vpro.domain.media.MediaIdentifiable;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks whether a {@link MediaIdentifiable} as at least one genre if that is required according to {@link MediaIdentifiable#getMediaType() media type}.{@link nl.vpro.domain.media.MediaType#requiresGenre() #requiresGenre()
 * @since 7.8
 * @author Michiel Meeuwissen
 */
@Target({  TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = HasGenreValidator.class)
@Documented
public @interface HasGenre {

    String message() default "{nl.vpro.constraints.hasgenre}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
