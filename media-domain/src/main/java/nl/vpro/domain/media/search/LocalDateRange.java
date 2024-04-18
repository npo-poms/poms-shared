/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "localdateRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class LocalDateRange implements Range<ChronoLocalDate> {

    @XmlElement
    private RangeValue<ChronoLocalDate> start;

    @XmlElement
    private RangeValue<ChronoLocalDate> stop;

    public LocalDateRange() {
    }

    public LocalDateRange(LocalDate start, LocalDate stop) {
        this.start = Value.of(start);
        this.stop = Value.exclusive(stop);
    }

    public LocalDateRange(LocalDate start) {
        this.start = Value.of(start);
        this.stop = Value.of(start);
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "localDateRangeValueType")
    public static class Value extends Range.RangeValue<ChronoLocalDate> {

        @XmlValue
        @XmlSchemaType(name = "dateTime")
        LocalDate value;

        public Value() {

        }
        public Value(String value) {
            this.value = LocalDate.parse(value);
        }


        @lombok.Builder
        public Value(Boolean inclusive, LocalDate value) {
            super(inclusive);
            this.value = value;
        }
        public static Value of(LocalDate instant) {
            if (instant == null) {
                return null;
            }
            return builder().value(instant).build();
        }

        public static Value exclusive(LocalDate instant) {
            if (instant == null) {
                return null;
            }
            return builder().value(instant).inclusive(false).build();
        }
    }

}
