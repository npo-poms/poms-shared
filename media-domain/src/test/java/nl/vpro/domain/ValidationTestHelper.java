/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import javax.validation.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

public class ValidationTestHelper {

    public static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


}
