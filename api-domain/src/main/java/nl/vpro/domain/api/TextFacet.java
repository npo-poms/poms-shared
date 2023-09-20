/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import javax.xml.bind.annotation.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "textFacetType")
@Setter
@Getter
public class TextFacet<T extends AbstractSearch<V>, V> extends AbstractFacet<T> implements LimitableFacet<T> {

    @XmlElement
    private Integer threshold = null;

    @XmlAttribute
    private FacetOrder sort = FacetOrder.VALUE_ASC;

    @XmlElement
    private Integer max = Constants.MAX_FACET_RESULTS;

    @XmlElement
    private String include;

    @XmlElement
    private String script;

    public TextFacet() {
    }

    public TextFacet(Integer threshold, FacetOrder sort, Integer max) {
        if(threshold != null) {
            this.threshold = threshold;
        }
        if(sort != null) {
            this.sort = sort;
        }
        if(max != null) {
            this.max = max;
        }
    }

    @Override
    public T getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(T filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "TextFacet{max=" + max + ", sort=" + sort + ", threshold=" + threshold + "}";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof TextFacet textFacet)) {
            return false;
        }

        if(max != null ? !max.equals(textFacet.max) : textFacet.max != null) {
            return false;
        }
        if(sort != textFacet.sort) {
            return false;
        }
        if (!Objects.equals(include, textFacet.include)) {
            return false;
        }
        return threshold != null ? threshold.equals(textFacet.threshold) : textFacet.threshold == null;
    }

    @Override
    public int hashCode() {
        int result = threshold != null ? threshold.hashCode() : 0;
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        return result;
    }
}
