/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class ExtendedTextFacet<T extends AbstractSearch> extends TextFacet<T> implements Facet<T> {

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
