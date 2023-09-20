/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.NameableSearchableLimitableFacet;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "pageRelationFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"threshold", "sort", "offset", "max", "name", "subSearch"})
public class RelationFacet extends ExtendedPageFacet implements NameableSearchableLimitableFacet<PageSearch, RelationSearch> {

    private String name;

    @Valid
    private RelationSearch subSearch;


    public RelationFacet() {
    }


    public RelationFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
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
}
