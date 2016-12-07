/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.util.LocationResult;

public interface LocationHandler {

    int score(Location location, String... pubOptions);

    LocationResult handle(Location location, HttpServletRequest request, String... pubOptions);


    default LocationResult handleIfSupports(Location location, HttpServletRequest request, String... pubOptions) {
        if (pubOptions == null) {
            pubOptions = new String[] {};
        }
        if (score(location, pubOptions) > 0) {
            return handle(location, request, pubOptions);
        } else {
            return null;
        }
    }
}
