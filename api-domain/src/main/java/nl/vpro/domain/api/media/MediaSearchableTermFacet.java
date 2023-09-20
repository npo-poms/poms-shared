/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.SearchableLimitableFacet;
import nl.vpro.domain.api.TermSearch;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaSearchableTermFacetType")
public class MediaSearchableTermFacet extends MediaFacet implements SearchableLimitableFacet<MediaSearch, TermSearch> {

    @Valid
    TermSearch subSearch;

    public MediaSearchableTermFacet() {
    }

    public MediaSearchableTermFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }
    @lombok.Builder(builderMethodName = "searchableBuilder")
    private MediaSearchableTermFacet(Integer threshold, FacetOrder sort, Integer max, TermSearch termSearch) {
        super(threshold, sort, max);
        this.subSearch = termSearch;
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
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
