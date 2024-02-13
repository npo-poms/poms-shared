/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.api.ExtendedTextFacet;
import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "extendedPageFacetType")
public class ExtendedPageFacet extends ExtendedTextFacet<PageSearch, Page> {
    public ExtendedPageFacet() {
    }

    public ExtendedPageFacet(Integer threshold, FacetOrder sort, Integer max) {
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
