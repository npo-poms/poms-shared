/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.NameableSearchableLimitableFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaRelationFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"threshold", "sort", "offset", "max", "name", "subSearch"})
public class RelationFacet extends ExtendedMediaFacet implements NameableSearchableLimitableFacet<MediaSearch, RelationSearch> {

    private String name;

    @Valid
    private RelationSearch subSearch;

    public RelationFacet() {
    }

    public RelationFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @lombok.Builder
    private RelationFacet(Integer threshold, FacetOrder sort, Integer max, RelationSearch subSearch, String name) {
        super(threshold, sort, max);
        this.subSearch = subSearch;
        this.name = name;
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public RelationSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(RelationSearch subSearch) {
        this.subSearch = subSearch;
    }

    @XmlAttribute
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "relation facet '" + name + "'" + subSearch;
    }
}
