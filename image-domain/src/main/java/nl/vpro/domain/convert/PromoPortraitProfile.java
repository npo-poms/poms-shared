/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface PromoPortraitProfile extends DynamicProfile<Geometry> {

    Geometry DEFAULT_GEOMETRY = Geometry.compile("1080", 2000);

    @Override
    default TestResult<Geometry> dynamicTest(@NonNull String request) {
        return new TestResult<>(request.equals("promo-portrait"), getGeometry());
    }

    Geometry getGeometry();
}
