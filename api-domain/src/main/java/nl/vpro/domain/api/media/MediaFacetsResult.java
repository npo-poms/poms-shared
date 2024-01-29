/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaFacetsResultType", propOrder = {
    "titles",
    "types",
    "avTypes",
    "sortDates",
    "broadcasters",
    "genres",
    "geoLocations",
    "tags",
    "durations",
    "descendantOf",
    "episodeOf",
    "memberOf",
    "relations",
    "ageRatings",
    "contentRatings"
})
@Getter
@Setter
public class MediaFacetsResult {

    private List<TermFacetResultItem> titles;

    private List<TermFacetResultItem> types;

    private List<TermFacetResultItem> avTypes;

    private List<DateFacetResultItem> sortDates;

    private List<TermFacetResultItem> broadcasters;

    private List<GenreFacetResultItem> genres;

    private List<TermFacetResultItem> tags;

    private List<DurationFacetResultItem> durations;

    private List<MemberRefFacetResultItem> descendantOf;

    private List<MemberRefFacetResultItem> episodeOf;

    private List<MemberRefFacetResultItem> memberOf;

    private List<MultipleFacetsResult> relations;

    private List<TermFacetResultItem> ageRatings;

    private List<TermFacetResultItem> contentRatings;

    private List<GeoLocationFacetResultItem> geoLocations;


    List<MemberRefFacetResultItem> getMemberOf(MediaSearch search) {
        if (search != null && search.getMemberOf() != null && memberOf == null) {
            memberOf = new ArrayList<>();
        }
        return memberOf;
    }


    List<TermFacetResultItem> getTypes(MediaSearch search) {
        if (search != null && search.getTypes() != null && types == null) {
            types = new ArrayList<>();
        }
        return types;
    }

    List<TermFacetResultItem> getAvTypes(MediaSearch search) {
        if (search != null && search.getAvTypes() != null && avTypes == null) {
            avTypes = new ArrayList<>();
        }
        return avTypes;
    }

    List<DateFacetResultItem> getSortDates(MediaSearch search) {
        if (search != null && search.getSortDates() != null && sortDates == null) {
            sortDates = new ArrayList<>();
        }
        return sortDates;
    }

    List<TermFacetResultItem> getBroadcasters(MediaSearch search) {
        if (search != null && search.getBroadcasters() != null && broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    List<GenreFacetResultItem> getGenres(MediaSearch search) {
        if (search != null && search.getGenres() != null && genres == null) {
            genres = new ArrayList<>();
        }
        return genres;
    }

    List<TermFacetResultItem> getTags(MediaSearch search) {
        if (search != null && search.getTags() != null && tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    List<DurationFacetResultItem> getDurations(MediaSearch search) {
        if (search != null && search.getDurations() != null && durations == null) {
            durations = new ArrayList<>();
        }
        return durations;
    }

    List<MemberRefFacetResultItem> getDescendantOf(MediaSearch search) {
        if (search != null && search.getDescendantOf() != null && descendantOf == null) {
            descendantOf = new ArrayList<>();
        }
        return descendantOf;
    }

    List<MemberRefFacetResultItem> getEpisodeOf(MediaSearch search) {
        if (search != null && search.getEpisodeOf() != null && memberOf == null) {
            memberOf = new ArrayList<>();
        }
        return memberOf;
    }

    List<MultipleFacetsResult> getRelations(MediaSearch search) {
        if (search != null && search.getRelations() != null && relations == null) {
            relations = new ArrayList<>();
        }
        return relations;
    }

    List<TermFacetResultItem> getAgeRating(MediaSearch search) {
        if (search != null && search.getAgeRatings() != null && ageRatings == null) {
            ageRatings = new ArrayList<>();
        }
        return ageRatings;
    }

    List<TermFacetResultItem> getContentRatings(MediaSearch search) {
        if (search != null && search.getContentRatings() != null && contentRatings == null) {
            contentRatings = new ArrayList<>();
        }
        return contentRatings;
    }

     List<TermFacetResultItem> getTitles(MediaSearch search) {
        if (search != null && search.getTitles() != null && titles == null) {
            titles = new ArrayList<>();
        }
        return titles;
    }
}
