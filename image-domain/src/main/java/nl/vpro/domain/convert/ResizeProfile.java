/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.domain.image.Dimension;

public interface ResizeProfile extends ParameterizedProfile<Geometry> {

    default int getMaxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
          if(!request.startsWith("s")) {
            return new TestResult<>(false, null);
        }

        final String arg = request.substring(1);
        Geometry geometry = Geometry.compile(arg, getMaxSize());

        return new TestResult<>(geometry.matches(), geometry);
    }

    @Override
    default @PolyNull Dimension convertedDimension(Object s, @PolyNull Dimension dimension) {
        if (dimension.getWidth() == null) {
            return null;
        }
        Geometry geometry = (Geometry) s;
        if (geometry.getModifier().contains(Geometry.Modifier.ONLY_WHEN_BIGGER)) {
            if (geometry.width() > dimension.getWidth()) {
                return dimension;
            }
        }
        float scale = (float) geometry.width() / dimension.getWidth();
        return Dimension.of(geometry.width(), Math.round(dimension.getHeight() * scale));
    }
}
