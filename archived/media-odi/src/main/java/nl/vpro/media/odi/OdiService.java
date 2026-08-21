/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import jakarta.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.media.odi.util.LocationResult;

/**
 * See https://jira.vpro.nl/browse/MSE-1788
 * <p>
 * TODO: Name is not correct, It does not do only 'odi'.
 *
 * @author Roelof Jan Koekoek
 * @since 1.8*
 */
public interface OdiService {

    LocationResult playMedia(MediaObject media, HttpServletRequest request, String... pubOptions);

    LocationResult playLocation(Location location, HttpServletRequest request, String... pubOptions);

    LocationResult playUrl(String url, HttpServletRequest request, String... pubOptions);

}
