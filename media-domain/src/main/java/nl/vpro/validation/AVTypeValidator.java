/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.*;

@Slf4j
public class AVTypeValidator implements ConstraintValidator<AVTypeValidation, MediaIdentifiable> {

    @Override
    public boolean isValid(MediaIdentifiable value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.getMediaType() == null) {
            return true;
        }
        MediaType mediaType = value.getMediaType();
        try {
            AVType avType = (AVType) value.getClass().getMethod("getAVType").invoke(value);
            return avType == null || avType.test(value);
        } catch (Exception  e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }
}
