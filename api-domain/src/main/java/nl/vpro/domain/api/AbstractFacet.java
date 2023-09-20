/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

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

    @Valid
    protected T filter;

}
