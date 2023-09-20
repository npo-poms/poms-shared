/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Instant;

import java.time.temporal.ChronoUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeFacetItemType")
public class DateRangeFacetItem implements RangeFacetItem<Instant> {

    private String name;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant begin;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant end;

    public DateRangeFacetItem() {
    }

    @lombok.Builder
    public DateRangeFacetItem(String name, Instant begin, Instant end) {
        this.name = name;
        // truncate, because should not be more precise than ES can do. Instant.now got more precise since java 9 (?).
        this.begin = begin == null ? null : begin.truncatedTo(ChronoUnit.MILLIS);
        this.end = end == null ? null : end.truncatedTo(ChronoUnit.MILLIS);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    @Override
    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "DateRangeFacetItem{name='" + name + "', begin=" + begin + ", end=" + end + '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof DateRangeFacetItem that)) {
            return false;
        }

        if(begin != null ? !begin.equals(that.begin) : that.begin != null) {
            return false;
        }
        if(end != null ? !end.equals(that.end) : that.end != null) {
            return false;
        }
        if(name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public boolean matches(Instant begin, Instant end) {
        return (this.begin == null || this.begin.equals(begin))
            &&
            (this.end == null || this.end.equals(end));
    }
}
