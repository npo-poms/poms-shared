package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

import jakarta.persistence.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.util.TimeUtils;

import static java.util.Comparator.comparing;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
public class ScheduleEventIdentifier implements Serializable, Comparable<ScheduleEventIdentifier> {

    @Serial
    private static final long serialVersionUID = -8254248336625205070L;

    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    protected Channel channel;

    @Column
    protected Instant start;

    @Column
    @Enumerated(EnumType.STRING)
    protected ScheduleEventType type;

    @Column
    protected String midRef;


    public ScheduleEventIdentifier() {
        // to help hibernate
    }

    public ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start) {
        this.start = start;
        this.channel = channel;
        if (channel == Channel.NVOD) {
            throw new IllegalArgumentException("NVOD events should have a mid");
        }
    }


    public ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start, String midRef) {
        this.start = start;
        this.channel = channel;
        if (channel != Channel.NVOD) {
            throw new IllegalArgumentException("Only NVOD events should have a mid");
        }
        this.midRef = midRef;
    }

    private ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start, @Nullable ScheduleEventType type, @Nullable String midRef) {
        this.start = start;
        this.channel = channel;
        this.type = type;
        this.midRef = midRef;
    }

    public Instant getStartInstant() {
        return start;
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
        if (type == ScheduleEventType.ON_DEMAND || that.type == ScheduleEventType.ON_DEMAND) {
            if (!Objects.equals(type, that.type)) {
                return false;
            }
            if (!Objects.equals(midRef, that.midRef)) {
                return false;
            }
        }
        if(start == null || that.start == null || start.equals(that.start)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (type == ScheduleEventType.ON_DEMAND) {
            return Objects.hash(channel, start, type, midRef);
        } else {
            return Objects.hash(channel, start);
        }
    }

    @Override
    public String toString() {
        return channel.name() + ":" + (start != null ? start.atZone(Schedule.ZONE_ID).toLocalDateTime() : "<null>");
    }

    public String asString() {
        return channel.getXmlValue() + ":" + start + (type == ScheduleEventType.ON_DEMAND ? (":" + midRef) : "");

    }

    public static ScheduleEventIdentifier parse(CharSequence id) {
        String[] split = id.toString().split(":", 3);
        if (split.length == 2) {
            return new ScheduleEventIdentifier(
                Channel.valueOfXml(split[0]),
                TimeUtils.parse(split[1]).orElseThrow(() -> new IllegalArgumentException("Could not parse " + id)
                ));

        } else {
            return new ScheduleEventIdentifier(
                Channel.valueOfXml(split[0]),
                TimeUtils.parse(split[1]).orElseThrow(() -> new IllegalArgumentException("Could not parse " + id)),
                split[2]
            );
        }

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
