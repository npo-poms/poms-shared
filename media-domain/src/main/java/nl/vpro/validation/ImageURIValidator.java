/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import java.util.regex.Matcher;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.support.Image;

public class ImageURIValidator implements ConstraintValidator<ImageURI, String> {

    @Override
    public boolean isValid(String imageUri, ConstraintValidatorContext constraintValidatorContext) {
        if(imageUri == null) {
            return true;
        }


        Matcher matcher = Image.SERVER_URI_PATTERN.matcher(imageUri);
        return matcher.find();
    }
}
