/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.TextFacet;
import nl.vpro.domain.page.Page;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "pageFacetType")
public class PageFacet extends TextFacet<PageSearch, Page> {
    public PageFacet() {
    }

    public PageFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @XmlElement
    @Override
    public PageSearch getFilter() {
        return this.filter;
    }

    @SuppressWarnings("RedundantMethodOverride") // It is not redundant, it is needed for JAXB
    @Override
    public void setFilter(PageSearch filter) {
        this.filter = filter;
    }
}
