/*
 * Copyright (C) 2017 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * TODO same as TermFacetResultItem without id?
 * @since 5.5
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterFacetResultItemType")
public class FilterFacetResultItem extends FacetResultItem {

    @Getter
    @Setter
    private String value;

    public FilterFacetResultItem() {
    }

    @lombok.Builder
    public FilterFacetResultItem(String value, long count) {
        super(count);
        this.value = value;
    }


}
