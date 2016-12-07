/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.AVFileFormat;
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

    private static final List<String> ODIP_OPTIONS = Arrays.asList("m3u8", "f4m");

    private List<LocationHandler> handlers;

    private LocationHandler finalHandler;

    private final Comparator<Location> defaultOrder = (first, second) -> {
        if(first.getAvFileFormat() != null && second.getAvFileFormat() != null) {
            int format = first.getAvFileFormat().compareTo(second.getAvFileFormat());
            if(format != 0) {
                return format;
            }
        }

        if(first.getBitrate() != null & second.getBitrate() != null) {
            // inverse: high -> low
            int bitrate = second.getBitrate().compareTo(first.getBitrate());
            if(bitrate != 0) {
                return bitrate;
            }
        }

        return first.getProgramUrl().compareTo(second.getProgramUrl());
    };

    @Override
    public LocationResult playMedia(MediaObject media, HttpServletRequest request, String... pubOptions) {
        if(pubOptions != null && pubOptions.length > 0) {
            final List<String> scoreList = new ArrayList<>(pubOptions.length);
            for(String pubOption : pubOptions) {
                if(pubOption.length() > 0) {
                    combinePubOption(pubOption, scoreList);
                }
            }

            Comparator<Location> comparator = (first, second) -> {
                int score = locationScore(scoreList, first) - locationScore(scoreList, second);
                return score != 0 ? score : defaultOrder.compare(first, second);
            };

            return handleLocations(media, comparator, request, pubOptions);
        } else {
            return handleLocations(media, defaultOrder, request, pubOptions);
        }
    }

    @Override
    public LocationResult playLocation(Location location, HttpServletRequest request, String... pubOptions) {
        LocationResult result = handleLocation(location, request);
        if(result != null) {
            return result;
        }

        if(hasFinalHandler()) {
            LocationResult finalResult = finalHandler.handleIfSupports(location, request);
            if(finalResult != null) {
                log.debug("No handler found to hande {}, using final handler {} resulted {}", location, finalHandler, finalResult);
                return finalResult;
            }
        }

        return null;
    }

    @Override
    public LocationResult playUrl(String url, HttpServletRequest request, String... pubOptions) {
        Location location = new Location(url, OwnerType.BROADCASTER);
        return playLocation(location, request, pubOptions);
    }

    private boolean hasFinalHandler() {
        return finalHandler != null;
    }

    public void addHandler(LocationHandler handler) {
        if(handlers == null) {
            handlers = new ArrayList<>(5);
        }
        handlers.add(handler);
    }

    public void setHandlers(List<LocationHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Optional final handler when no handlers matches. When empty a 404 is returned on a no match scenario.
     *
     * @param finalHandler
     */
    public void setFinalHandler(LocationHandler finalHandler) {
        this.finalHandler = finalHandler;
    }

    @PostConstruct
    public void log() {
        log.info("using {} (final handler: {})", handlers, finalHandler);
    }

    private LocationResult handleLocations(MediaObject media, Comparator<Location> comparator, HttpServletRequest request, String... pubOptions) {
        SortedSet<Location> locations = new TreeSet<>(comparator);
        locations.addAll(media.getLocations());

        for(Location location : locations) {
            LocationResult result = handleLocation(location, request, pubOptions);
            if(result != null) {
                return result;
            }
        }

        if(hasFinalHandler()) {
            for(Location location : locations) {
                LocationResult result = finalHandler.handleIfSupports(location, request, pubOptions);
                if(result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private LocationResult handleLocation(Location location, HttpServletRequest request, String... pubOptions) {
        for(LocationHandler handler : handlers) {
            LocationResult result = handler.handleIfSupports(location, request, pubOptions);
            if (result != null) {
                log.debug("Found result {} for handler {}", result, handler);
                return result;
            }
        }
        return null;
    }

    private int locationScore(List<String> pubOptions, Location location) {
        int formatScore = pubOptions.indexOf(location.getAvFileFormat().name().toLowerCase());

        String url = location.getProgramUrl().toLowerCase();
        int urlScore = -1;
        for(String pubOption : pubOptions) {
            if(url.contains(pubOption)) {
                urlScore = pubOptions.indexOf(pubOption);
                break;
            }
        }

        int score = formatScore >= urlScore ? formatScore : urlScore;
        // No score == -1. Lower is better, therefore no score == worst possible result
        return score != -1 ? score : pubOptions.size();
    }

    private void combinePubOption(String pubOption, List<String> scoreList) {
        if(ODIP_OPTIONS.contains(pubOption)) {
            scoreList.add(AVFileFormat.HASP.name().toLowerCase());
        }

        scoreList.add(pubOption);
    }
}
