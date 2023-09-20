/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Instant;

import nl.vpro.domain.api.DateRangeFacets;
import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.RangeFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaFacetsBuilder {

    private final MediaFacets facets = new MediaFacets();

    public static MediaFacetsBuilder facets() {
        return new MediaFacetsBuilder();
    }

    public MediaFacetsBuilder titles() {
        facets.setTitles(new TitleFacetList());
        return this;
    }

    public MediaFacetsBuilder titles(Integer threshold, FacetOrder sort, Integer max) {
        TitleFacetList titleFacets  = new TitleFacetList();
        titleFacets.setThreshold(threshold);
        titleFacets.setSort(sort);
        titleFacets.setMax(max);
        return this;
    }

    public MediaFacetsBuilder types() {
        facets.setTypes(new MediaFacet());
        return this;
    }

    public MediaFacetsBuilder types(Integer threshold, FacetOrder sort, Integer max) {
        facets.setTypes(new MediaFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder avTypes() {
        facets.setAvTypes(new MediaFacet());
        return this;
    }

    public MediaFacetsBuilder avTypes(Integer threshold, FacetOrder sort, Integer max) {
        facets.setAvTypes(new MediaFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder sortDates() {
        facets.setSortDates(new DateRangeFacets<>());
        return this;
    }

    @SafeVarargs
    public final MediaFacetsBuilder sortDates(RangeFacet<Instant>... facetItems) {
        facets.setSortDates(new DateRangeFacets<>(facetItems));
        return this;
    }

    public MediaFacetsBuilder broadcasters() {
        facets.setBroadcasters(new MediaFacet());
        return this;
    }

    public MediaFacetsBuilder broadcasters(Integer threshold, FacetOrder sort, Integer max) {
        facets.setBroadcasters(new MediaFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder genres() {
        facets.setGenres(new MediaSearchableTermFacet());
        return this;
    }

    public MediaFacetsBuilder genres(Integer threshold, FacetOrder sort, Integer max) {
        facets.setGenres(new MediaSearchableTermFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder tags() {
        facets.setTags(new ExtendedMediaFacet());
        return this;
    }

    public MediaFacetsBuilder tags(Integer threshold, FacetOrder sort, Integer max) {
        facets.setTags(new ExtendedMediaFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder memberOf() {
        facets.setMemberOf(new MemberRefFacet());
        return this;
    }

    public MediaFacetsBuilder memberOf(Integer threshold, FacetOrder sort, Integer max) {
        facets.setMemberOf(new MemberRefFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder memberOf(MemberRefFacet facet) {
        facets.setMemberOf(facet);
        return this;
    }

    public MediaFacetsBuilder episodeOf() {
        facets.setEpisodeOf(new MemberRefFacet());
        return this;
    }

    public MediaFacetsBuilder episodeOf(Integer threshold, FacetOrder sort, Integer max) {
        facets.setEpisodeOf(new MemberRefFacet(threshold, sort, max));
        return this;
    }

    public MediaFacetsBuilder episodeOf(MemberRefFacet facet) {
        facets.setEpisodeOf(facet);
        return this;
    }

    public MediaFacetsBuilder descendantOf() {
        facets.setDescendantOf(new MemberRefFacet());
        return this;
    }

    public MediaFacetsBuilder descendantOf(MemberRefFacet facet) {
        facets.setDescendantOf(facet);
        return this;
    }

    public MediaFacetsBuilder filter(MediaSearch search) {
        facets.setFilter(search);
        return this;
    }

    public MediaFacets build() {
        return facets;
    }
}
