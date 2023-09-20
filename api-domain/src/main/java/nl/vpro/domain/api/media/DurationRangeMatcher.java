/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DefaultDurationXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeMatcherType", propOrder = {"begin", "end"})
@JsonPropertyOrder({"begin", "end", "inclusiveEnd", "match"})
public class DurationRangeMatcher extends SimpleRangeMatcher<Duration> {

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.SerializerString.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @XmlSchemaType(name = "duration")
    private Duration begin;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.SerializerString.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @XmlSchemaType(name = "duration")
    private Duration end;


    public DurationRangeMatcher() {
    }

    public DurationRangeMatcher(Duration begin, Duration end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    public DurationRangeMatcher(Duration begin, Duration end) {
        super(begin, end, null);
    }


    @lombok.Builder
    public DurationRangeMatcher(Duration begin, Duration end, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd, match);
    }


    @Override
    protected boolean defaultIncludeEnd() {
        return false;
    }
}
