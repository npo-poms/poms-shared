/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.TextFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "pageFacetType")
public class PageFacet extends TextFacet<PageSearch> {
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

    @Override
    public void setFilter(PageSearch filter) {
        this.filter = filter;
    }
}
