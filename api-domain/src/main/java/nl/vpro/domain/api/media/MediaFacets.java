/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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
    private MediaSearchableTermFacet geoLocations;


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
            || contentRatings != null
            || geoLocations != null;
    }

}
