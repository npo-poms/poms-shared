/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.ExtendedTextMatcher;
import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.SortableForm;
import nl.vpro.domain.api.media.bind.MediaSortTypeAdapter;
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
public class MediaForm extends AbstractMediaForm implements SortableForm, Predicate<MediaObject> {


    @XmlElement
    @XmlJavaTypeAdapter(MediaSortTypeAdapter.class)
    @JsonIgnore
    @Valid
    private LinkedHashMap<MediaSortField, Order> sortFields;


    @Override
    public boolean isSorted() {
        return sortFields != null && !sortFields.isEmpty();
    }

    @JsonProperty("sort")
    public Map<MediaSortField, Order> getSortFields() {
        return sortFields;
    }

    public void setSortFields(Map<MediaSortField, Order> sortFields) {
        if(sortFields != null) {
            this.sortFields = new LinkedHashMap<>(sortFields);
        }
    }

    public void addSortField(MediaSortOrder order) {
        if(sortFields == null) {
            sortFields = new LinkedHashMap<>(3);
        }

        sortFields.put(order.getSortField(), order.getOrder());
    }

}
