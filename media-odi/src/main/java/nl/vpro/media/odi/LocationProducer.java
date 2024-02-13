/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.util.LocationResult;


/**
 * A location producer make a {@link LocationResult} given a certain {@link Location} and 'pubOptions'
 */
public interface LocationProducer {

    /**
     * Higher is better. On default (but see {@link #supports}) 0 means that the producer cannot produce a location result for this location
     * The result of this will be filled in the LocationResult, which determins its basic natural order.
     */
    int score(Location location, String... pubOptions);

    /**
     * Produce a {@link LocationResult}. In principal never <code>null</code>
     */
    LocationResult produce(Location location, HttpServletRequest request, String... pubOptions);


    /**
     * Given the result of {@link #score} can then producer actually produce?
     */
    default boolean supports(int score) {
        return score > 0;
    }

    default Optional<LocationResult> produceIfSupports(
        @NotNull Location location,
        HttpServletRequest request,
        @NotNull String... pubOptions) {
        if (pubOptions == null) {
            pubOptions = new String[0];
        }
        int score = score(location, pubOptions);
        if (supports(score)) {
            LocationResult result = produce(location, request, pubOptions);
            if (result == null) {
                return Optional.empty();
            }
            result.setScore(score);
            result.setProducer(this.getClass().getSimpleName());
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
