/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.Child;
import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.TextualObjects;
import nl.vpro.domain.media.*;
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
public class ScheduleEventUpdate implements Comparable<ScheduleEventUpdate>, TextualObjectUpdate<TitleUpdate, DescriptionUpdate, ScheduleEventUpdate>, Child<ProgramUpdate> {

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


    @XmlTransient
    @Getter
    @Setter
    ProgramUpdate parent;

    public ScheduleEventUpdate() {
    }

    public ScheduleEventUpdate(Channel channel, Instant start, Duration  duration) {
        this.channel = channel;
        this.start = start;
        this.duration = duration;
    }


    @lombok.Builder(builderClassName = "Builder")
    private ScheduleEventUpdate(Channel channel, Instant start, Duration  duration, ProgramUpdate media) {
        this.channel = channel;
        this.start = start;
        this.duration = duration;
        this.parent = media;
    }

    public ScheduleEventUpdate(ScheduleEvent event) {
        this(event.getChannel(), event.getStartInstant(), event.getDuration());

        TextualObjects.copyToUpdate(event, this);
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
        TextualObjects.copyAndRemove(this, event, ownerType);
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

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
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
    @XmlElementWrapper(name = "titles", required = false)
    @XmlElement(name = "title")
    public SortedSet<TitleUpdate> getTitles() {
        return titles;
    }

    @Override
    public void setTitles(SortedSet<TitleUpdate> titles) {
        this.titles = titles;
    }



    public void setTitles(TitleUpdate... titles) {
        this.titles = new TreeSet<>(Arrays.asList(titles));
    }


    @Override
    public BiFunction<String, TextualType, TitleUpdate> getTitleCreator() {
        return TitleUpdate::new;

    }

    @Override
    public ScheduleEventUpdate addTitle(String title, @Nonnull TextualType type) {
        if (titles == null) {
            titles = new TreeSet<>();
        }
        getTitles().add(getTitleCreator().apply(title, type));
        return this;
    }

    @Override
    @XmlElementWrapper(name = "descriptions", required = false)
    @XmlElement(name = "description")
    public SortedSet<DescriptionUpdate> getDescriptions() {
        return descriptions;
    }

    @Override
    public void setDescriptions(SortedSet<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }

    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    @Override
    public BiFunction<String, TextualType, DescriptionUpdate> getDescriptionCreator() {
        return DescriptionUpdate::new;

    }

    @Override
    public ScheduleEventUpdate addDescription(String description, @Nonnull TextualType type) {
        if (descriptions == null) {
            descriptions  = new TreeSet<>();
        }
        getDescriptions().add(getDescriptionCreator().apply(description, type));
        return this;
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



    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof Program) {
            this.parent = (ProgramUpdate) parent;
        }
    }


    public static class Builder {

        public Builder localStart(int year, int month, int day, int hour, int minute) {
            return localStart(LocalDateTime.of(year, month, day, hour, minute));
        }

        public Builder localStart(LocalDateTime localDateTime) {
            return start(localDateTime.atZone(Schedule.ZONE_ID).toInstant());
        }

    }

}
