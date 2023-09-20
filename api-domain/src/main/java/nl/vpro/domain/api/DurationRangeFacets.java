/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.DurationRangeFacetsToJson;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "durationRangeFacetsType", propOrder = {
    "ranges"})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(using = DurationRangeFacetsToJson.Serializer.class)
@JsonDeserialize(using = DurationRangeFacetsToJson.Deserializer.class)
public class DurationRangeFacets<T extends AbstractSearch> extends AbstractFacet<T> implements Facet<T> {

    @XmlElements({
        @XmlElement(name = "interval", type = DurationRangeInterval.class),
        @XmlElement(name = "range", type = DurationRangeFacetItem.class)
    })
    @JsonIgnore
    private List<RangeFacet<Duration>> ranges;

    public DurationRangeFacets() {
    }

    @SafeVarargs
    public DurationRangeFacets(RangeFacet<Duration>... ranges) {
        if(ranges != null && ranges.length > 0) {
            this.ranges = Arrays.asList(ranges);
        }
    }

    public List<RangeFacet<Duration>> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeFacet<Duration>> ranges) {
        this.ranges = ranges;
    }

    @SafeVarargs
    public final void addRanges(RangeFacet<Duration>... ranges) {
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

    @Override
    public String toString() {
        return "DurationRangeFacets{" +
            "ranges=" + ranges +
            ", filter=" + filter +
            '}';
    }
}

