/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.groups.Default;

import nl.vpro.domain.media.update.Validation;
import nl.vpro.validation.PomsValidatorGroup;
import nl.vpro.validation.WarningValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ValidationTestHelper {



     public static <T> java.util.Set<javax.validation.ConstraintViolation<T>> validate(T o, boolean warnings, int expected) {
        Set<ConstraintViolation<T>> validate = validate(o, warnings);
        assertThat(validate).hasSize(expected);
        log.info("{}", validate);
        return validate;
    }

    public static <T> java.util.Set<javax.validation.ConstraintViolation<T>> validate(T o, boolean warnings) {
        if (warnings) {
            return Validation.getValidator().validate(o, PomsValidatorGroup.class, Default.class, WarningValidatorGroup.class);
        } else {
            return Validation.getValidator().validate(o, PomsValidatorGroup.class, Default.class);
        }
    }

    public static <T> java.util.Set<javax.validation.ConstraintViolation<T>> validateProperty(T o, String propertyName, boolean warnings) {
        if (warnings) {
            return Validation.getValidator().validateProperty(o, propertyName, PomsValidatorGroup.class, Default.class, WarningValidatorGroup.class);
        } else {
            return Validation.getValidator().validateProperty(o, propertyName,  PomsValidatorGroup.class, Default.class);
        }
    }

    public static <T> java.util.Set<javax.validation.ConstraintViolation<T>> validate(T o) {
        return validate(o, true);
    }


    public static <T> java.util.Set<javax.validation.ConstraintViolation<T>> dbValidate(T o) {
        return Validation.getValidator().validate(o, Default.class);
    }


}
