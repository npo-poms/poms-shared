/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

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

    protected T filter;

    @Override
    public abstract T getFilter();

    @Override
    public abstract void setFilter(T filter);

/*
    @JsonIgnore
    public MediaSearch getMediaSearch() {
        return filter != null && filter instanceof MediaSearch ? (MediaSearch)filter : null;
    }

    public void setMediaSearch(MediaSearch search) {
        this.filter = (T)search;
    }

    @JsonIgnore
    public PageSearch getPageSearch() {
        return filter != null && filter instanceof PageSearch ? (PageSearch)filter : null;
    }

    public void setPageSearch(PageSearch search) {
        this.filter = (T)search;
    }
*/
}
