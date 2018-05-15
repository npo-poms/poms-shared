package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.Schedule;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
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
        @Singular
        Collection<ScheduleEventUpdate> scheduleEvents
        ) {
        this.channel = channel;
        this.start = Schedule.of(start, localStart, startDay);
        this.stop = Schedule.of(stop, localStop, stopDay);
        this.scheduleEvents = new TreeSet<>();
        if (scheduleEvents != null) {
            this.scheduleEvents.addAll(scheduleEvents);
        }

    }


    public Range<ZonedDateTime> asRange() {
        return Range.openClosed(
            getStart().atZone(Schedule.ZONE_ID),
            getStop().atZone(Schedule.ZONE_ID)
        );
    }

    @Override
    @Nonnull
    public Iterator<ScheduleEventUpdate> iterator() {
        return getScheduleEvents().iterator();
    }


}
