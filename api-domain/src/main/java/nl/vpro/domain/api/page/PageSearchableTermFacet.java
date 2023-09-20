/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.SearchableLimitableFacet;
import nl.vpro.domain.api.TermSearch;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "pageSearchableTermFacetType")
public class PageSearchableTermFacet extends PageFacet implements SearchableLimitableFacet<PageSearch, TermSearch> {

    @Valid
    TermSearch subSearch;

    public PageSearchableTermFacet() {
    }

    public PageSearchableTermFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @lombok.Builder
    private PageSearchableTermFacet(Integer threshold, FacetOrder order, Integer max, PageSearch filter, TermSearch subSearch) {
        this(threshold, order, max);
        setFilter(filter);
        setSubSearch(subSearch);
    }


    @Override
    @XmlElement
    public TermSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TermSearch subSearch) {
        this.subSearch = subSearch;
    }
}
