/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Predicate;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.SortableForm;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlRootElement(name = "mediaForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaFormType",
    propOrder = {
        // Intellij warnings are incorrect since parent class is @XmlTransient
        "searches",
        "sortFields",
        "facets"})
@ToString
@EqualsAndHashCode(callSuper = true)
public class MediaForm extends AbstractMediaForm implements SortableForm, Predicate<MediaObject> {


    public static MediaFormBuilder builder() {
        return MediaFormBuilder.form();
    }

    @Valid
    @Setter
    @XmlElements({
        @XmlElement(name = "sort", type = MediaSortOrder.class),
        @XmlElement(name = "titleSort", type = TitleSortOrder.class)
    })
    @XmlElementWrapper(name = "sortFields")
    @JsonIgnore
    private MediaSortOrderList sortFields;

    @XmlElement
    @Valid
    private MediaFacets facets;


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

    @JsonProperty("sort")
    public MediaSortOrderList getSortFields() {
        return sortFields;
    }

    @Override
    public boolean isSorted() {
        return sortFields != null && !sortFields.isEmpty();
    }

    public void addSortField(MediaSortOrder order) {
        if(sortFields == null) {
            sortFields = new MediaSortOrderList();
        }

        sortFields.add(order);
    }

    public boolean hasSearches() {
        return getSearches() != null && getSearches().hasSearches();
    }

}
