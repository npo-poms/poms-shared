/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.TextFacet;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaFacetType")
public class MediaFacet extends TextFacet<MediaSearch, MediaObject> {
    public MediaFacet() {
    }

    public MediaFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }
    @lombok.Builder
    private MediaFacet(Integer threshold, FacetOrder sort, Integer max, MediaSearch filter) {
        this(threshold, sort, max);
        this.filter = filter;
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
