/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.validation.*;

import nl.vpro.domain.media.MidIdentifiable;

/**
 * @since 7.8
 * @see HasGenre
 */
@Slf4j
public class HasGenreValidator implements ConstraintValidator<HasGenre, MidIdentifiable> {


    HasGenre annotation;

	@Override
    public void initialize(HasGenre constraintAnnotation) {
        this.annotation = constraintAnnotation;
	}

    @Override
    public boolean isValid(MidIdentifiable hasType, ConstraintValidatorContext context) {
        boolean requiresGenre = hasType != null && hasType.getMediaType() != null &&  hasType.getMediaType().requiresGenre();
        if (requiresGenre) {
            try {
                Collection<?> genres = (Collection<?>) hasType.getClass().getMethod("getGenres").invoke(hasType);
                return ! genres.isEmpty();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.warn(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }


}
