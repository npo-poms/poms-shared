/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author rico
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "termFacetResultItemType")
@Getter
@Setter
public class TermFacetResultItem extends FacetResultItem {

    private String id;

    private String value;

    public TermFacetResultItem() {
    }

    @lombok.Builder
    public TermFacetResultItem(String value, String id, long count) {
        super(count);
        this.id = id;
        this.value = value;
    }



    @Override
    public String toString() {
        return id + ":" + count;
    }

}
