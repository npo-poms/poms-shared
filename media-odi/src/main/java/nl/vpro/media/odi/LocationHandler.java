/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.util.LocationResult;

public interface LocationHandler {

    boolean supports(Location location, String... pubOptions);

    LocationResult handle(Location location, HttpServletRequest request, String... pubOptions);

}
