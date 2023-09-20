/*
 * Copyright (C) 2023 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface PromoLandscapeProfile extends Profile<Geometry> {


    Geometry DEFAULT_GEOMETRY = Geometry.compile("1920", 2000);

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
        return new TestResult<>(request.equals("promo-landscape"), getGeometry());
    }

    default Geometry getGeometry() {
        return DEFAULT_GEOMETRY;
    }
}
