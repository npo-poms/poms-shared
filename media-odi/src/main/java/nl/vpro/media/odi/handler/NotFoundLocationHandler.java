/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationHandler;
import nl.vpro.media.odi.util.LocationResult;

/**
 * Returns not found message.
 * <p/>
 * See https://jira.vpro.nl/browse/MSE-1788
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class NotFoundLocationHandler implements LocationHandler {

    @Override
    public int score(Location location, String... pubOptions) {
        return 1;
    }

    @Override
    public LocationResult handle(Location location, HttpServletRequest request, String... pubOptions) {
        return null;
    }
}
