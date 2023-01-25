/*
 * Copyright (C) 2023 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface CropProfile extends DynamicProfile<Geometry> {


    int getMaxSize();

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
        boolean matches;
        if(!request.startsWith("c")) {
            return new TestResult<>(false, null);
        }

        String arg = request.substring(1);
        Geometry geometry = Geometry.compile(arg, getMaxSize());

        if(!geometry.matches()) {
            return new TestResult<>(false, geometry);
        }
        return new TestResult<>(true, geometry);
    }
}
