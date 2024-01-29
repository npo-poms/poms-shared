/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;
import java.util.function.BiFunction;

import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.validator.constraints.time.DurationMin;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.*;
import nl.vpro.xml.bind.*;

/**
 * @see nl.vpro.domain.media.update
 * @see ScheduleEvent
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventUpdateType",
    propOrder = {
        "start",
        "guideDay",
        "duration",
        "repeat",
        "titles",
        "descriptions"
        })

@JsonPropertyOrder({
    "channel",
    "start",
    "guideDate",
    "duration",
    "repeat",
    "avAttributes",
    "offset",
})
@Getter
@Setter
@Slf4j
public class ScheduleEventUpdate implements Comparable<ScheduleEventUpdate>, TextualObjectUpdate<TitleUpdate, DescriptionUpdate, ScheduleEventUpdate>, Child<MediaUpdate<?>> {

    @XmlAttribute(required = true)
    @NotNull
    private Channel channel;

    @XmlAttribute
    private String net;


    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @NotNull
    private Instant start;

    /**
     * @since 5.9
     */
    @XmlElement(required = false)
    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @JsonDeserialize(using = StringZonedLocalDateToJsonTimestamp.Deserializer.class)
    //    @JsonSerialize(using = StringZonedLocalDateToJsonTimestamp.Serializer.class) Serializing as millis is too silly.
    private LocalDate guideDay;

    /**
     * @since 7.7
     */
    @Embedded
    @XmlElement
    protected RepeatUpdate repeat;


    @XmlElement(required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = DurationToJsonTimestamp.XmlSerializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    @NotNull(message = "duration is required")
    @DurationMin
    private Duration duration;


    @Valid
    private SortedSet<TitleUpdate> titles;

    @Valid
    private SortedSet<DescriptionUpdate> descriptions;


    @XmlTransient
    @Getter
    @Setter
    MediaUpdate<?>  parent;

    public ScheduleEventUpdate() {
    }

    public ScheduleEventUpdate(Channel channel, Instant start, Duration  duration) {
        this.channel = channel;
        this.start = start;
        this.duration = duration;
    }


    @lombok.Builder(builderClassName = "Builder")
    private ScheduleEventUpdate(
        @NonNull Channel channel,
        @NonNull Instant start,
        LocalDate guideDay,
        Duration  duration,
        Repeat repeat,
        @Singular SortedSet<TitleUpdate> titles,
        @Singular SortedSet<DescriptionUpdate> descriptions,
        ProgramUpdate media) {
        this.channel = channel;
        this.start = start;
        this.guideDay = guideDay;
        this.duration = duration;
        this.repeat = RepeatUpdate.of(repeat);
        this.titles = titles;
        this.descriptions = descriptions;
        this.parent = media;
    }

    public ScheduleEventUpdate(MediaUpdate<?> media, ScheduleEvent event) {
        this(event.getChannel(),
            event.getStartInstant(),
            event.getGuideDate(),
            event.getDuration(),
            event.getRepeat(),
            null,
            null,
            null
        );

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
        this.parent = media;
    }

    public ScheduleEventUpdate(ScheduleEvent event) {
        this(null, event);
    }

    public ScheduleEvent toScheduleEvent(OwnerType ownerType) {
        if (channel == null) {
            log.info("No channel in {}, cannot be converted to schedulevent", this);
            return null;
        }
        ScheduleEvent event = new ScheduleEvent(channel, net == null ? null : new Net(net), start, duration);
        if (parent != null) {
            event.setMidRef(parent.getMid());
        }
        event.setGuideDate(this.guideDay);
        event.setRepeat(RepeatUpdate.asRepeat(this.repeat));
        TextualObjects.copyAndRemove(this, event, ownerType);
        return event;
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

    @XmlTransient
    public void setTitles(TitleUpdate... titles) {
        this.titles = new TreeSet<>(Arrays.asList(titles));
    }


    @Override
    public BiFunction<String, TextualType, TitleUpdate> getTitleCreator() {
        return TitleUpdate::new;

    }

    @Override
    public ScheduleEventUpdate addTitle(String title, @NonNull TextualType type) {
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

    @XmlTransient
    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    @Override
    public BiFunction<String, TextualType, DescriptionUpdate> getDescriptionCreator() {
        return DescriptionUpdate::new;

    }

    @Override
    public ScheduleEventUpdate addDescription(
        @Nullable String description, @NonNull TextualType type) {
        if (StringUtils.isNotEmpty(description)) {
            if (descriptions == null) {
                descriptions  = new TreeSet<>();
            }
            getDescriptions().add(getDescriptionCreator().apply(description, type));
        }
        return this;
    }

    @Override
    public String toString() {
        return channel + " " + (start == null ? null : start.atZone(Schedule.ZONE_ID).toLocalDateTime());
    }


    @Override
    public int compareTo(ScheduleEventUpdate o) {
        return Objects.compare(toScheduleEvent(OwnerType.BROADCASTER), o.toScheduleEvent(OwnerType.BROADCASTER),
            Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    public Range<Instant> asRange() {
        return Range.closedOpen(start, start.plus(duration));
    }
    public void setRange(Range<Instant> range) {
        this.start = range.lowerEndpoint();
        this.duration = Duration.between(range.lowerEndpoint(), range.upperEndpoint());
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof ProgramUpdate) {
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
