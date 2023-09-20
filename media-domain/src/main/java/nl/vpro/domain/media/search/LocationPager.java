/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;
import static nl.vpro.domain.media.search.LocationSortField.lastModified;

@SuppressWarnings("WSReferenceInspection")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationPagerType", namespace = SEARCH_NAMESPACE, propOrder = {
        "offset",
        "max",
        "sort",
        "order"
        })
public class LocationPager extends Pager<LocationSortField> {


    @lombok.Builder
    public LocationPager(long offset, Integer max, LocationSortField sort, Direction order) {
        super(offset, max, sort, order);
    }

    public LocationPager() {
        super(0, null, lastModified, Direction.ASC);
    }

    @Override
    @XmlElement
    public LocationSortField getSort() {
        return super.getSort();
    }

    @Override
    public void setSort(LocationSortField sort) {
        super.setSort(sort);
    }
}
