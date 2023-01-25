/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 */
public interface ColorizeHexProfile extends ParameterizedProfile<Matcher> {

    Pattern COLORIZE_HEX = Pattern.compile("^ch([\\dA-F]{2})([\\dA-F]{2})([\\dA-F]{2})$");

    @Override
    default TestResult<Matcher> dynamicTest(@NonNull String request) {
        Matcher matcher = COLORIZE_HEX.matcher(request);

        return new TestResult<>(matcher.find(), matcher);
    }

}
