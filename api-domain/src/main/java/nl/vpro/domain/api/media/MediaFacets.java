/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

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
    private DateRangeFacets<?> sortDates;

    @XmlElement
    private MediaFacet broadcasters;

    @XmlElement
    private MediaSearchableTermFacet genres;

    @XmlElement
    private ExtendedMediaFacet tags;

    @XmlElement
    private DurationRangeFacets<?> durations;

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendToString(builder, "titles", titles);
        appendToString(builder, "types", types);
        appendToString(builder, "avTypes", avTypes);
        appendToString(builder, "sortDates", sortDates);
        appendToString(builder, "broadcasters", broadcasters);
        appendToString(builder, "genres", genres);
        appendToString(builder, "tags", tags);
        appendToString(builder, "durations", durations);
        appendToString(builder, "descendantOf", descendantOf);
        appendToString(builder, "episodeOf", episodeOf);
        appendToString(builder, "memberOf", memberOf);
        appendToString(builder, "relations", relations);
        appendToString(builder, "memberOf", memberOf);
        appendToString(builder, "ageRatings", ageRatings);
        appendToString(builder, "contentRatings", contentRatings);
        appendToString(builder, "geoLocations", geoLocations);
        appendToString(builder, "filter", filter);
        builder.insert(0, "MediaFacet{");
        builder.append("}");
        return builder.toString();
    }
    private void appendToString(StringBuilder builder, String fieldName, Object value) {
        if (value != null) {
            if (builder.length() > 0){
                builder.append(", ");
            }
            builder.append(fieldName).append("=").append(value);
        }

    }

}
