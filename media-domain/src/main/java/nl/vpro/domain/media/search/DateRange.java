/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.media.Schedule;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@Builder
public class DateRange implements Range<Instant> {

    @XmlElement
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant start;

    @XmlElement
    @XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant stop;

    public  DateRange() {
    }

    public DateRange(LocalDateTime start, LocalDateTime stop) {
        this.start = start == null ? null : start.atZone(Schedule.ZONE_ID).toInstant();
        this.stop = stop == null ? null : stop.atZone(Schedule.ZONE_ID).toInstant();
    }


}
