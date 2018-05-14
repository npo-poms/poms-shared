package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.SortedSet;

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



    public ScheduleUpdate(Channel channel, Instant start, Instant stop) {
        this.channel = channel;
        this.start = start;
        this.stop = stop;

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
