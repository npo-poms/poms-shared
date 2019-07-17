package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ScheduleUpdate implements Iterable<ScheduleEventUpdate> {


    @XmlTransient // See property
    @Getter
    protected SortedSet<ScheduleEventUpdate> scheduleEvents;

    @XmlAttribute
    @Getter
    @Setter
    protected Channel channel;

    @XmlAttribute
    @Getter
    @Setter
    protected Net net;

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


    public Range<LocalDateTime> asLocalRange() {
        return Range.closedOpen(
            getStart().atZone(Schedule.ZONE_ID).toLocalDateTime(),
            getStop().atZone(Schedule.ZONE_ID).toLocalDateTime()
        );
    }

    public Range<Instant> asRange() {
        return Range.closedOpen(
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

}
