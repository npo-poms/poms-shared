/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import lombok.ToString;

import java.net.URI;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationProducer;
import nl.vpro.media.odi.util.LocationResult;

/**
 * Returns the programUrl for requested location.
 * <p/>
 * See https://jira.vpro.nl/browse/MSE-1788
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@ToString
public class MatchSchemesLocationHandler implements LocationProducer {

    final List<String> schemes;

    public MatchSchemesLocationHandler(List<String> schemes) {
        this.schemes = schemes;
    }

    @Override
    public int score(Location location, String... pubOptions) {
        if (schemes.contains(URI.create(location.getProgramUrl()).getScheme())) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {
        return LocationResult.of(location);
    }
}
