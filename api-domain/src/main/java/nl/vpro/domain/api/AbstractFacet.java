/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.media.MediaFacet;
import nl.vpro.domain.api.page.PageFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
// Apply the JAXB data binding via override in subclass to map the corresponding subtype of T
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "abstractFacetType")
@XmlSeeAlso({MediaFacet.class, PageFacet.class})
public abstract class AbstractFacet<T extends AbstractSearch> implements Facet<T>, FilteredFacet<T> {

    @Getter
    @Setter
    @Valid
    protected T filter;

}
