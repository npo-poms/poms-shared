/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import nl.vpro.domain.media.Location;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class LocationAssert extends PublishableObjectAssert<LocationAssert, Location> {

    public LocationAssert(Location actual) {
        super(actual, LocationAssert.class);
    }
}
