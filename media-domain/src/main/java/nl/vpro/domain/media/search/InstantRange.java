/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
public class InstantRange implements Range<Instant, InstantRange.Value> {

    @XmlElement
    private Value start;

    @XmlElement
    private Value stop;

    public InstantRange() {
    }

    public InstantRange(LocalDateTime start, LocalDateTime stop) {
        this.start = start == null ? null : Value.of(start.atZone(Schedule.ZONE_ID).toInstant());
        this.stop = stop == null ? null : Value.of(stop.atZone(Schedule.ZONE_ID).toInstant());
    }

    public InstantRange(Instant start, Instant stop) {
        this.start = Value.of(start);
        this.stop = Value.of(stop);
    }

    public static class Builder {
        public Builder startInstance(Instant start) {
            return start(Value.of(start));
        }

        public Builder stopInstance(Instant stop) {
            return stop(Value.of(stop));
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "dateRangeValueType")
    public static class Value extends Range.RangeValue<Instant> {

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
        public static Value of(Instant instant) {
            if (instant == null) {
                return null;
            }
            return builder().value(instant).build();
        }
    }

}
