/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.ExtendedTextFacet;
import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "extendedMediaFacetType")
public class ExtendedMediaFacet extends ExtendedTextFacet<MediaSearch, MediaObject> {
    public ExtendedMediaFacet() {
    }

    public ExtendedMediaFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @XmlElement
    @Override
    public MediaSearch getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(MediaSearch filter) {
        this.filter = filter;
    }
}
