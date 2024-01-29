/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.SearchableLimitableFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "memberRefFacetType")
public class MemberRefFacet extends MediaFacet implements SearchableLimitableFacet<MediaSearch, MemberRefSearch> {

    @Valid
    MemberRefSearch subSearch;

    public MemberRefFacet() {
    }

    public MemberRefFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @lombok.Builder(builderMethodName = "refBuilder")
    private MemberRefFacet(Integer threshold, FacetOrder sort, Integer max, MemberRefSearch subSearch) {
        super(threshold, sort, max);
        this.subSearch = subSearch;
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public MemberRefSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(MemberRefSearch subSearch) {
        this.subSearch = subSearch;
    }
}
