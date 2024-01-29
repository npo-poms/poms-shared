package nl.vpro.domain.media.update;

import lombok.*;

import java.time.*;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.util.Ranges;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 * @see nl.vpro.domain.media.update
 */
@XmlAccessorType(XmlAccessType.NONE)

public class ScheduleUpdate implements Iterable<ScheduleEventUpdate> {


    @XmlTransient // See property
    protected SortedSet<@Valid ScheduleEventUpdate> scheduleEvents;

    @XmlAttribute
    @Getter
    @Setter
    protected Channel channel;

    @XmlAttribute
    @Getter
    @Setter
    protected String net;

    @XmlAttribute(name = "start")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Getter
    @Setter
    protected Instant start;

    @XmlAttribute(name = "stop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Getter
    @Setter
    protected Instant stop;


    public ScheduleUpdate() {
        // constructor required by jaxb.
    }

    @lombok.Builder
    private ScheduleUpdate(
        Channel channel,
        Instant start,
        Instant stop,
        LocalDateTime localStart,
        LocalDateTime localStop,
        LocalDate startDay,
        LocalDate stopDay,
        @Singular Collection<ScheduleEventUpdate> scheduleEvents
        ) {
        this.channel = channel;
        this.start = Schedule.of(start, localStart, startDay);
        this.stop = Schedule.of(stop, localStop, stopDay);
        this.scheduleEvents = new TreeSet<>();
        if (scheduleEvents != null) {
            this.scheduleEvents.addAll(scheduleEvents);
        }
    }


    @NonNull
    public SortedSet<ScheduleEventUpdate> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        return scheduleEvents;
    }


    public Range<LocalDateTime> asLocalRange() {
        return Ranges.closedOpen(
            DateUtils.toLocalDateTime(getStart(), Schedule.ZONE_ID),
            DateUtils.toLocalDateTime(getStop(), Schedule.ZONE_ID)
        );
    }

    public Range<Instant> asRange() {
        return Ranges.closedOpen(
            getStart(),
            getStop()
        );
    }

    @Override
    @NonNull
    public Iterator<ScheduleEventUpdate> iterator() {
        return getScheduleEvents().iterator();
    }


    @Override
    public String toString() {
        return getScheduleEvents().size() + " events in " + asLocalRange();
    }

    public Schedule fetch(OwnerType ownerType) {
        Schedule schedule = Schedule.builder()
            .start(start)
            .start(stop)
            .channel(channel)
            .build();
        for (ScheduleEventUpdate scheduleEventUpdate : scheduleEvents) {
            schedule.addScheduleEvent(
                scheduleEventUpdate.toScheduleEvent(ownerType)
            );
        }
        return schedule;
    }

    public void add(ScheduleUpdate schedule) {
        if (schedule.getChannel() != channel) {
            throw new IllegalArgumentException();
        }
        if (schedule.start.isBefore(start)) {
            this.start = schedule.start;
        }
        if (schedule.stop.isAfter(stop)) {
            this.stop = schedule.stop;
        }
        this.scheduleEvents.addAll(schedule.scheduleEvents);
    }

}
