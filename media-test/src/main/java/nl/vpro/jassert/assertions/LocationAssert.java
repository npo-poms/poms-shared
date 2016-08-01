/**
 * Copyright (C) 2012 All rights reserved
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
