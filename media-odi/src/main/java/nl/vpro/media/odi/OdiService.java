/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.media.odi.util.LocationResult;

/**
 * See https://jira.vpro.nl/browse/MSE-1788
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public interface OdiService {

    LocationResult playMedia(MediaObject media, HttpServletRequest request, String... pubOptions);

    LocationResult playLocation(Location location, HttpServletRequest request, String... pubOptions);

    LocationResult playUrl(String url, HttpServletRequest request, String... pubOptions);

}
