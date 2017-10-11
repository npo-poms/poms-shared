/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.DateRangeFacets;
import nl.vpro.domain.api.DurationRangeFacets;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaFacetsType")
public class MediaFacets {

    @XmlElement
    private TitleFacetList titles;

    @XmlElement
    private MediaFacet types;

    @XmlElement
    private MediaFacet avTypes;

    @XmlElement
    private DateRangeFacets sortDates;

    @XmlElement
    private MediaFacet broadcasters;

    @XmlElement
    private MediaSearchableTermFacet genres;

    @XmlElement
    private ExtendedMediaFacet tags;

    @XmlElement
    private DurationRangeFacets durations;

    @XmlElement
    private MemberRefFacet descendantOf;

    @XmlElement
    private MemberRefFacet episodeOf;

    @XmlElement
    private MemberRefFacet memberOf;

    @XmlElement
    private RelationFacetList relations;

    @XmlElement
    private MediaFacet ageRatings;

    @XmlElement
    private MediaFacet contentRatings;


    @XmlElement
    private MediaSearch filter;


    public boolean isFaceted() {
        return titles != null
            || types != null
            || avTypes != null
            || sortDates != null
            || broadcasters != null
            || genres != null
            || tags != null
            || durations != null
            || descendantOf != null
            || episodeOf != null
            || memberOf != null
            || relations != null
            || ageRatings != null
            || contentRatings != null;
    }

    public TitleFacetList getTitles() {
        return titles;
    }

    public void setTitles(TitleFacetList titles) {
        this.titles = titles;
    }

    public MediaFacet getTypes() {
        return types;
    }

    public void setTypes(MediaFacet types) {
        this.types = types;
    }

    public MediaFacet getAvTypes() {
        return avTypes;
    }

    public void setAvTypes(MediaFacet avTypes) {
        this.avTypes = avTypes;
    }

    public DateRangeFacets getSortDates() {
        return sortDates;
    }

    public void setSortDates(DateRangeFacets sortDates) {
        this.sortDates = sortDates;
    }

    public MediaFacet getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(MediaFacet broadcasters) {
        this.broadcasters = broadcasters;
    }

    public MediaSearchableTermFacet getGenres() {
        return genres;
    }

    public void setGenres(MediaSearchableTermFacet genres) {
        this.genres = genres;
    }

    public ExtendedMediaFacet getTags() {
        return tags;
    }

    public void setTags(ExtendedMediaFacet tags) {
        this.tags = tags;
    }

    public DurationRangeFacets getDurations() {
        return durations;
    }

    public void setDurations(DurationRangeFacets durations) {
        this.durations = durations;
    }

    public MemberRefFacet getDescendantOf() {
        return descendantOf;
    }

    public void setDescendantOf(MemberRefFacet descendantOf) {
        this.descendantOf = descendantOf;
    }

    public MemberRefFacet getEpisodeOf() {
        return episodeOf;
    }

    public void setEpisodeOf(MemberRefFacet episodeOf) {
        this.episodeOf = episodeOf;
    }

    public MemberRefFacet getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(MemberRefFacet memberOf) {
        this.memberOf = memberOf;
    }

    public RelationFacetList getRelations() {
        return relations;
    }

    public void setRelations(RelationFacetList relations) {
        this.relations = relations;
    }

    public MediaSearch getFilter() {
        return filter;
    }

    public void setFilter(MediaSearch filter) {
        this.filter = filter;
    }

    public MediaFacet getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(MediaFacet ageRating) {
        this.ageRatings = ageRating;
    }

    public MediaFacet getContentRatings() {
        return contentRatings;
    }

    public void setContentRatings(MediaFacet contentRatings) {
        this.contentRatings = contentRatings;
    }
}
