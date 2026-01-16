/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

import jakarta.xml.bind.annotation.*;

import nl.vpro.util.XmlValued;

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

    /**
     * @since 8.12
     */
    public static <T extends TermFacetResultItem> T find(XmlValued xmlValued, Collection<T> collection) {
        return find(xmlValued.getXmlValue(), collection);
    }
    /**
     * @since 8.12
     */
    public static <T extends TermFacetResultItem> T find(String id, Collection<T> collection) {
        return collection.stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElseThrow();
    }

}
