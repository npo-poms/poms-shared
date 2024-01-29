/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Duration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DefaultDurationXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "durationRangeFacetItemType")
public class DurationRangeFacetItem implements RangeFacetItem<Duration> {

    private String name;

    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter .class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.SerializerString.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlSchemaType(name = "duration")
    private Duration begin;

    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter .class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.SerializerString.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlSchemaType(name = "duration")
    private Duration end;

    public DurationRangeFacetItem() {
    }

    @lombok.Builder
    public DurationRangeFacetItem(String name, Duration begin, Duration end) {
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Duration getBegin() {
        return begin;
    }

    public void setBegin(Duration begin) {
        this.begin = begin;
    }

    @Override
    public Duration getEnd() {
        return end;
    }

    public void setEnd(Duration end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "DurationRangeFacetItem{name='" + name + "', begin=" + begin + ", end=" + end + '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof DurationRangeFacetItem that)) {
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
    public boolean matches(Duration begin, Duration end) {
        return (this.begin == null || this.begin.equals(begin))
            &&
            (this.end == null || this.end.equals(end));
    }
}
