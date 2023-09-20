/*
 * Copyright (C) 2023 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface CropProfile extends ParameterizedProfile<Geometry> {


    default int getMaxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
        boolean matches;
        if(!request.startsWith("c")) {
            return new TestResult<>(false, null);
        }

        String arg = request.substring(1);
        Geometry geometry = Geometry.compile(arg, getMaxSize());

        return new TestResult<>(geometry.matches(), geometry);
    }
}
