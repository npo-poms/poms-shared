/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.odi.util.LocationResult;

/**
 * See https://jira.vpro.nl/browse/MSE-1788
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@Slf4j
public class OdiServiceImpl implements OdiService {

    private List<LocationProducer> handlers;

    private List<LocationSorter> sorters;


    @Override
    public LocationResult playMedia(MediaObject media, HttpServletRequest request, String... pubOptions) {
        return
            handleLocations(media, request, pubOptions)
                .stream()
                .min(sorter(pubOptions))
                .orElse(null);
    }

    @Override
    public LocationResult playLocation(Location location, HttpServletRequest request, String... pubOptions) {
        return
            handleLocation(location, request, pubOptions)
                .stream()
                .min(sorter(pubOptions))
                .orElse(null);
    }

    protected Comparator<LocationResult> sorter(String... pubOptions) {
        Comparator<LocationResult> result = null;
        if (sorters != null) {
            for (LocationSorter comp : sorters) {
                if (result == null) {
                    result = comp.getComparator(pubOptions);
                } else {
                    result = result.thenComparing(comp.getComparator(pubOptions));
                }
            }
        }
        if (result == null) {
            return Comparator.naturalOrder();
        } else {
            return result.thenComparing(Comparator.naturalOrder());
        }
    }

    @Override
    public LocationResult playUrl(String url, HttpServletRequest request, String... pubOptions) {
        Location location = new Location(url, OwnerType.BROADCASTER);
        return playLocation(location, request, pubOptions);
    }

    public void setHandlers(List<LocationProducer> handlers) {
        this.handlers = handlers;
    }


    public void setSorters(List<LocationSorter> sorters) {
        this.sorters = sorters;
    }


    @PostConstruct
    public void log() {
        log.info("using {}", handlers);
    }

    List<LocationResult> handleLocations(MediaObject media, HttpServletRequest request, String... pubOptions) {
        List<LocationResult> result = new ArrayList<>();
        media.getLocations().stream()
            .map(l -> handleLocation(l, request, pubOptions))
            .forEach(result::addAll);
        return result;
    }

    List<LocationResult> handleLocation(Location location, HttpServletRequest request, String... pubOptions) {
        return
            handlers.stream()
                .map(h -> h.produceIfSupports(location, request, pubOptions))
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
