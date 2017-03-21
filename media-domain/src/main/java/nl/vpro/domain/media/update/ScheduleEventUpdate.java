/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.ScheduleEventDescription;
import nl.vpro.domain.media.support.ScheduleEventTitle;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventUpdateType",
    propOrder = {
        "start",
        "duration",
        "titles",
        "descriptions"
        })
public class ScheduleEventUpdate implements Comparable<ScheduleEventUpdate>, TextualObjectUpdate<TitleUpdate, DescriptionUpdate, ScheduleEventUpdate> {

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

    @Valid
    private SortedSet<TitleUpdate> titles;

    @Valid
    private SortedSet<DescriptionUpdate> descriptions;

    private ScheduleEventUpdate() {
    }
    @Deprecated
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
        if (event.getTitles() != null) {
            SortedSet<TitleUpdate> titleSet = getTitles();
            for (ScheduleEventTitle et : event.getTitles()) {
                titleSet.add(TitleUpdate.of(et));
            }
        }
        if (event.getDescriptions() != null) {
            SortedSet<DescriptionUpdate> descriptionSet = getDescriptions();
            for (ScheduleEventDescription et : event.getDescriptions()) {
                descriptionSet.add(DescriptionUpdate.of(et));
            }
        }
    }

    public ScheduleEvent toScheduleEvent(OwnerType ownerType) {
        ScheduleEvent event = new ScheduleEvent(channel, net == null ? null : new Net(net), start, duration);
        if (titles != null) {
            for (TitleUpdate up : titles) {
                event.addTitle(up.get(), ownerType, up.getType());
            }
        }
        if (descriptions != null) {
            for (DescriptionUpdate up : descriptions) {
                event.addDescription(up.get(), ownerType, up.getType());
            }
        }
        return event;
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

    @XmlElementWrapper(name = "titles", required = false)
    @XmlElement(name = "title")
    public SortedSet<TitleUpdate> getTitles() {
        return titles;
    }

    public void setTitles(SortedSet<TitleUpdate> titles) {
        this.titles = titles;
    }

    public void setTitles(TitleUpdate... titles) {
        this.titles = new TreeSet<>(Arrays.asList(titles));
    }

    public void setMainTitle(String title) {
        setTitle(title, TextualType.MAIN);
    }

    public void setTitle(String title, TextualType type) {
        for (TitleUpdate t : getTitles()) {
            if (t.getType() == type) {
                t.setTitle(title);
                return;
            }
        }
        getTitles().add(new TitleUpdate(title, type));
    }

    @XmlElementWrapper(name = "descriptions", required = false)
    @XmlElement(name = "description")
    public SortedSet<DescriptionUpdate> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(SortedSet<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }

    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    public void setMainDescription(String description) {
        setDescription(description, TextualType.MAIN);
    }

    public void setDescription(String description, TextualType type) {
        for (DescriptionUpdate t : getDescriptions()) {
            if (t.getType() == type) {
                t.setDescription(description);
                return;
            }
        }
        getDescriptions().add(new DescriptionUpdate(description, type));
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
        return toScheduleEvent(OwnerType.BROADCASTER).compareTo(o.toScheduleEvent(OwnerType.BROADCASTER));
    }
}
