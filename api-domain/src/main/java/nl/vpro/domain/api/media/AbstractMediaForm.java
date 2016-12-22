/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.Form;
import nl.vpro.domain.api.FormUtils;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */

@XmlTransient
public class AbstractMediaForm implements Form, Predicate<MediaObject> {

    @JsonProperty("highlight")
    @XmlAttribute(name = "highlight")
    private Boolean highlighted = null;

    @XmlElement
    @Valid
    private MediaSearch searches;

    @XmlElement
    @Valid
    private MediaFacets facets;


    @Override
    public String getText() {
        return FormUtils.getText(searches);
    }


    @Override
    public boolean isFaceted() {
        return facets != null && facets.isFaceted();
    }

    public MediaFacets getFacets() {
        return facets;
    }

    public void setFacets(MediaFacets facets) {
        this.facets = facets;
    }

    @Override
    public boolean isHighlight() {
        return highlighted != null ? highlighted : false;
    }

    public void setHighlight(boolean highlight) {
        this.highlighted = highlight;
    }

    public MediaSearch getSearches() {
        return searches;
    }

    public void setSearches(MediaSearch searches) {
        this.searches = searches;
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        return searches == null || searches.test(input);
    }
}
