/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.util.LocationResult;


/**
 * TODO
 */
public interface LocationHandler {

    /**
     * TODO
     */
    boolean supports(Location location, String... pubOptions);

    /**
     * TODO
     */
    LocationResult handle(Location location, HttpServletRequest request, String... pubOptions);


    default LocationResult handleIfSupports(Location location, HttpServletRequest request, String... pubOptions) {
        if (supports(location, pubOptions)) {
            return handle(location, request, pubOptions);
        } else {
            return null;
        }
    }
}
