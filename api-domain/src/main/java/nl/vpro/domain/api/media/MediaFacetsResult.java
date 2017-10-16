/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

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
    "tags",
    "durations",
    "descendantOf",
    "episodeOf",
    "memberOf",
    "relations",
    "ageRatings",
    "contentRatings",

})
public class MediaFacetsResult {


    private List<FacetResultItem> titles;

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

    public List<FacetResultItem> getTitles() {
        return titles;
    }

    public void setTitles(List<FacetResultItem> titles) {
        this.titles = titles;
    }

    public List<TermFacetResultItem> getTypes() {
        return types;
    }

    public void setTypes(List<TermFacetResultItem> types) {
        this.types = types;
    }

    public List<TermFacetResultItem> getAvTypes() {
        return avTypes;
    }

    public void setAvTypes(List<TermFacetResultItem> avTypes) {
        this.avTypes = avTypes;
    }

    public List<DateFacetResultItem> getSortDates() {
        return sortDates;
    }

    public void setSortDates(List<DateFacetResultItem> sortDates) {
        this.sortDates = sortDates;
    }

    public List<TermFacetResultItem> getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(List<TermFacetResultItem> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public List<GenreFacetResultItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreFacetResultItem> genres) {
        this.genres = genres;
    }

    public List<TermFacetResultItem> getTags() {
        return tags;
    }

    public void setTags(List<TermFacetResultItem> tags) {
        this.tags = tags;
    }

    public List<DurationFacetResultItem> getDurations() {
        return durations;
    }

    public void setDurations(List<DurationFacetResultItem> durations) {
        this.durations = durations;
    }

    public List<MemberRefFacetResultItem> getDescendantOf() {
        return descendantOf;
    }

    public void setDescendantOf(List<MemberRefFacetResultItem> descendantOf) {
        this.descendantOf = descendantOf;
    }

    public List<MemberRefFacetResultItem> getEpisodeOf() {
        return episodeOf;
    }

    public void setEpisodeOf(List<MemberRefFacetResultItem> episodeOf) {
        this.episodeOf = episodeOf;
    }

    public List<MemberRefFacetResultItem> getMemberOf() {
        return memberOf;
    }

    public List<MemberRefFacetResultItem> getMemberOf(MediaSearch search) {
        if (search != null && search.getMemberOf() != null && memberOf == null) {
            memberOf = new ArrayList<>();
        }
        return memberOf;
    }

    public void setMemberOf(List<MemberRefFacetResultItem> memberOf) {
        this.memberOf = memberOf;
    }

    public List<MultipleFacetsResult> getRelations() {
        return relations;
    }

    public void setRelations(List<MultipleFacetsResult> relations) {
        this.relations = relations;
    }

    public List<TermFacetResultItem> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(List<TermFacetResultItem> ageRatings) {
        this.ageRatings = ageRatings;
    }

    public List<TermFacetResultItem> getContentRatings() {
        return contentRatings;
    }

    public void setContentRatings(List<TermFacetResultItem> contentRatings) {
        this.contentRatings = contentRatings;
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
}
