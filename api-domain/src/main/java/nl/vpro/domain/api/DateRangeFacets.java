/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.DateRangeFacetsToJson;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Setter
@Getter
@XmlType(name = "dateRangeFacetsType", propOrder = {
    "ranges"})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(using = DateRangeFacetsToJson.Serializer.class)
@JsonDeserialize(using = DateRangeFacetsToJson.Deserializer.class)
public class DateRangeFacets<T extends AbstractSearch<?>> extends AbstractFacet<T> implements Facet<T> {

    @XmlElements({
        @XmlElement(name = "interval", type = DateRangeInterval.class),
        @XmlElement(name = "preset", type = DateRangePreset.class),
        @XmlElement(name = "range", type = DateRangeFacetItem.class)
    })
    @JsonIgnore
    private List<RangeFacet<Instant>> ranges;

    public DateRangeFacets() {
    }

    @SafeVarargs
    public DateRangeFacets(RangeFacet<Instant>... ranges) {
        if(ranges != null && ranges.length > 0) {
            this.ranges = Arrays.asList(ranges);
        }
    }

    @SafeVarargs
    public final void addRanges(RangeFacet<Instant>... ranges) {
       if(this.ranges == null) {
            this.ranges = new ArrayList<>(ranges.length);
        }

        Collections.addAll(this.ranges, ranges);
    }

    @Override
    public T getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(T filter) {
        this.filter = filter;
    }
}
