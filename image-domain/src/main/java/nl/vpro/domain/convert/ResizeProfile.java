/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.image.Dimension;

public interface ResizeProfile extends DynamicProfile<Geometry> {

    int getMaxSize();

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
          if(!request.startsWith("s")) {
            return new TestResult<>(false, null);
        }

        final String arg = request.substring(1);
        Geometry geometry = Geometry.compile(arg, getMaxSize());

        if(!geometry.matches()) {
            return new TestResult<>(false, geometry);
        }
        return new TestResult<>(true, geometry);
    }

    @Override
    default Dimension convertedDimension(Geometry s, Dimension dimension) {
        // TODO
        return null;//dimension;
    }
}
