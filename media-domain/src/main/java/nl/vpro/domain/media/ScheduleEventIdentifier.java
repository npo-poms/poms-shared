package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import javax.persistence.*;

import nl.vpro.util.DateUtils;
import nl.vpro.util.TimeUtils;

import static java.util.Comparator.comparing;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
public class ScheduleEventIdentifier implements Serializable, Comparable<ScheduleEventIdentifier> {

    private static final long serialVersionUID = -8254248336625205070L;

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

    @Override
    public int compareTo(ScheduleEventIdentifier o) {
        Instant otherStart = o.start;
        if (start != null
            && otherStart != null
            && (!start.equals(otherStart))) {

            return start.compareTo(o.start);
        }

        Channel otherChannel = o.getChannel();
        if (getChannel() != null && otherChannel != null) {
            return getChannel().ordinal() - otherChannel.ordinal();
        } else {
            return comparing(ScheduleEventIdentifier::getStartInstant, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ScheduleEventIdentifier::getChannel, Comparator.nullsLast(Comparator.naturalOrder())).compare(this, o);
        }
    }
}
