package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import nl.vpro.util.DateUtils;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
public class ScheduleEventIdentifier implements Serializable {

    @Column
    @Enumerated(EnumType.STRING)
    protected Channel channel;

    @Column
    protected Instant start;

    public ScheduleEventIdentifier() {
        // to help hibernate
    }

    public ScheduleEventIdentifier(Channel channel, Instant start) {
        this.start = start;
        this.channel = channel;
    }


    @Deprecated
    public ScheduleEventIdentifier(Channel channel, Date start) {
        this(channel, DateUtils.toInstant(start));
    }

    public Channel getChannel() {
        return channel;
    }

    public Instant getStartInstant() {
        return start;
    }

    @Deprecated
    public Date getStart() {
        return DateUtils.toDate(start);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduleEventIdentifier that = (ScheduleEventIdentifier)o;

        if(channel == null || that.channel == null || channel != that.channel) {
            return false;
        }
        if(start == null || that.start == null || !(start.toEpochMilli() == that.start.toEpochMilli())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = channel == null ? 0 : channel.hashCode();
        result = 31 * result + (start == null ? 0 : start.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return channel + ":" + start;
    }

    public String asString() {
        return channel.name() + ":" + start;
    }

    public static ScheduleEventIdentifier parse(String id) {
        String[] split = id.split("\\:", 2);
        return new ScheduleEventIdentifier(Channel.valueOf(split[0]), TimeUtils.parse(split[1]).orElseThrow(() -> new IllegalArgumentException("Could not parse " + id)));

    }

}
