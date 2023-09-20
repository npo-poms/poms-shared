/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;

/**
 * Used on fields that may return multiple facets e.g., relations. The field name does not suffice in these cases.
 *
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "namedTermFacetResultItemType")
public class MultipleFacetsResult implements Nameable, Iterable<TermFacetResultItem>  {

    @XmlAttribute
    private String name;

    @XmlElement(name = "facet")
    private List<TermFacetResultItem> facets;

    public MultipleFacetsResult() {
    }

    public MultipleFacetsResult(String name, List<TermFacetResultItem> facets) {
        this.name = name;
        this.facets = facets;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<TermFacetResultItem> getFacets() {
        if (facets == null) {
            facets = new ArrayList<>();
        }
        return facets;
    }

    public void setFacets(List<TermFacetResultItem> facets) {
        this.facets = facets;
    }

    @NonNull
    @Override
    public Iterator<TermFacetResultItem> iterator() {
        if (facets == null) {
            return Collections.emptyIterator();
        } else {            return facets.iterator();
        }
    }

    @Override
    public String toString () {
        return "facet result '" + name + "' " + facets;
    }
}
