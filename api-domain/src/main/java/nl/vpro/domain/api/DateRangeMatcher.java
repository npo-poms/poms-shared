/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.Schedule;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "dateRangeMatcherType", propOrder = {"begin", "end"})
@JsonPropertyOrder({"begin", "end", "inclusiveEnd", "match"})
public class DateRangeMatcher extends SimpleRangeMatcher<Instant> {

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlDocumentation("Json representation is millis since epoch, but supports natty parsing too")
    private Instant begin;


    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlDocumentation("Json representation is millis since epoch, but supports natty parsing too")
    private Instant end;


    public DateRangeMatcher() {
    }

    public DateRangeMatcher(Instant begin, Instant end) {
        super(begin, end);
    }

    @lombok.Builder(builderClassName = "Builder")
    public DateRangeMatcher(Instant begin, Instant end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    public DateRangeMatcher(Instant begin, Instant end, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd, match);
    }

    @Override
    protected boolean defaultIncludeEnd() {
        return false;
    }

    public static class Builder {
        public Builder localBegin(LocalDateTime localDateTime) {
            return begin(localDateTime.atZone(Schedule.ZONE_ID).toInstant());
        }
        public Builder localEnd(LocalDateTime localDateTime) {
            return end(localDateTime.atZone(Schedule.ZONE_ID).toInstant());
        }
    }
}
