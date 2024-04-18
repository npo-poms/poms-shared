/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.Schedule;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.TimeUtils;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class InstantRange implements Range<Instant> {

    @XmlElement
    private RangeValue<Instant> start;

    @XmlElement
    private RangeValue<Instant> stop;

    public InstantRange() {
    }

    /**
     * Creates a new range. Using {@link Schedule#ZONE_ID}.
     * @param start Inclusive start of the new range
     * @param stop  Exclusive stop of the new range
     */
    public InstantRange(LocalDateTime start, LocalDateTime stop) {
        this(start == null ? null : start.atZone(Schedule.ZONE_ID).toInstant(),
            stop == null ? null : stop.atZone(Schedule.ZONE_ID).toInstant());
    }

    /**
     * Creates a new range.
     * @param start Inclusive start of the new range
     * @param stop  Exclusive stop of the new range
     */
    public InstantRange(Instant start, Instant stop) {
        this.start = Value.of(start);
        this.stop = Value.exclusive(stop);
    }

    public static class Builder {
        public Builder startInstance(Instant start) {
            return start(Value.of(start));
        }

        public Builder stopInstance(Instant stop) {
            return stop(Value.of(stop));
        }
    }

    /**
     * Represents one of the endpoints of an {@link InstantRange}.
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "dateRangeValueType")
    public static class Value extends RangeValue<Instant> {

        @XmlValue
        @XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
        @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
        @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
        @XmlSchemaType(name = "dateTime")
        Instant value;

        public Value() {

        }
        public Value(String value) {
            this.value = TimeUtils.parse(value).orElse(null);
        }

        public Value(Long value) {
            this.value = value == null ? null : Instant.ofEpochMilli(value);
        }


        @lombok.Builder
        public Value(Boolean inclusive, Instant value) {
            super(inclusive);
            this.value = value;
        }
        public static Value exclusive(Instant instant){
            if (instant == null) {
                return null;
            }
            return builder().value(instant).inclusive(false).build();
        }
        public static Value of(Instant instant) {
            if (instant == null) {
                return null;
            }
            return builder().value(instant).build();
        }
    }

}
