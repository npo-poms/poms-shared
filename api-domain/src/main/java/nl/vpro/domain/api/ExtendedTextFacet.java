/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class ExtendedTextFacet<T extends AbstractSearch<V>, V> extends TextFacet<T, V> implements Facet<T> {

    @XmlAttribute
    private Boolean caseSensitive;


    public ExtendedTextFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    public ExtendedTextFacet() {
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    @XmlTransient
    public boolean isCaseSensitive() {
        return caseSensitive == null || caseSensitive;
    }
}
