package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

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
    @Getter
    protected Channel channel;

    @Column
    protected Instant start;

    public ScheduleEventIdentifier() {
        // to help hibernate
    }

    public ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start) {
        this.start = start;
        this.channel = channel;
    }


    @Deprecated
    public ScheduleEventIdentifier(Channel channel, Date start) {
        this(channel, DateUtils.toInstant(start));
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
        if(start == null || that.start == null || (start.toEpochMilli() != that.start.toEpochMilli())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, start);
    }

    @Override
    public String toString() {
        return channel + ":" + start;
    }

    public String asString() {
        return channel.name() + ":" + start;
    }

    public static ScheduleEventIdentifier parse(String id) {
        String[] split = id.split(":", 2);
        return new ScheduleEventIdentifier(Channel.valueOf(split[0]), TimeUtils.parse(split[1]).orElseThrow(() -> new IllegalArgumentException("Could not parse " + id)));

    }

}
