/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;

import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

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
@EqualsAndHashCode(callSuper = true)
public class MediaForm extends AbstractMediaForm implements SortableForm, Predicate<MediaObject> {

    public static MediaFormBuilder builder() {
        return MediaFormBuilder.form();
    }

    @Valid
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

    public void setSortFields(MediaSortOrderList sortFields) {
        this.sortFields = sortFields;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("sortFields", sortFields)
            .add("facets", facets)
            .add("search", getSearches())
            .omitNullValues()
            .toString();
    }

    /**
     * {@code null}-safe version of {@link #isFaceted()}
     * @since 7.1
     */
    public static boolean isFaceted(@Nullable MediaForm mediaForm){
        return mediaForm != null && mediaForm.isFaceted();
    }

    /**
     * {@code null}-safe version of {@link #isSorted()} ()}
     * @since 7.1
     */
    public static boolean isSorted(@Nullable MediaForm mediaForm){
        return mediaForm != null && mediaForm.isSorted();
    }
}
