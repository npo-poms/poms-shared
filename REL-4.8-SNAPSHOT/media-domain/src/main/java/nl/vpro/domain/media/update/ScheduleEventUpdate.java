/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventUpdateType",
    propOrder = {
        "start",
        "duration"
        })
public class ScheduleEventUpdate implements Comparable<ScheduleEventUpdate> {

    @XmlAttribute(required = true)
    private Channel channel;

    @XmlAttribute
    private String net;

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant start;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    private Duration duration;

    private ScheduleEventUpdate() {
    }

    public ScheduleEventUpdate(Channel channel, Date start, Date duration) {
        this(channel, instant(start), duration(duration));
    }

    public ScheduleEventUpdate(Channel channel, Instant start, Duration  duration) {
        this.channel = channel;
        this.start = start;
        this.duration = duration;
    }

    public ScheduleEventUpdate(ScheduleEvent event) {
        this(event.getChannel(), event.getStartInstant(), event.getDurationTime());
    }

    public ScheduleEvent toScheduleEvent() {
        return new ScheduleEvent(channel, net == null ? null : new Net(net), start, duration);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    @Deprecated
    public Date getStart() {
        return start == null ? null : Date.from(start);
    }

    @Deprecated
    public void setStart(Date start) {
        this.start = instant(start);
    }

    public Instant getStartInstant() {
        return start;
    }

    public void setStartInstant(Instant start) {
        this.start = start;
    }

    public Duration getDurationTime() {
        return duration;
    }

    public Date getDuration() {
        return duration == null ? null : new Date(duration.toMillis());
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration == null ? null : Duration.ofMillis(duration.getTime());
    }

    @Override
    public String toString() {
        return channel + " " + start;
    }


    private static Duration duration(Date duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofMillis(duration.getTime());
    }

    private static Instant instant(Date instant) {
        if (instant == null) {
            return null;
        }
        return instant.toInstant();
    }

    @Override
    public int compareTo(ScheduleEventUpdate o) {
        return toScheduleEvent().compareTo(o.toScheduleEvent());
    }
}
