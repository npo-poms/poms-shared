package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
public class ScheduleEventIdentifier implements Serializable, Comparable<ScheduleEventIdentifier> {

    @Serial
    private static final long serialVersionUID = -8254248336625205070L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @NotNull
    protected Channel channel;

    @Column(nullable = false)
    protected Instant start;

    @Column(nullable = false)
    protected String midRef;



    public ScheduleEventIdentifier() {
        // to help hibernate
    }

    public ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start) {
        // Normalize to milliseconds to match typical database timestamp precision
        this.start = start.truncatedTo(ChronoUnit.MILLIS);
        this.channel = channel;
        if (channel.isOnDemand()) {
            throw new IllegalArgumentException("NVOD events should have a mid");
        }
        this.midRef = "";
    }


    public ScheduleEventIdentifier(@NonNull Channel channel, @NonNull Instant start, @NonNull @ValidMid String midRef) {
        // Normalize to milliseconds to match typical database timestamp precision
        this.start = start.truncatedTo(ChronoUnit.MILLIS);
        this.channel = channel;
        if (! channel.isOnDemand()) {
            throw new IllegalArgumentException("Only NVOD events should have a mid");
        }
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
        // channel must match
        return  Objects.equals(channel, that.channel) &&
            Objects.equals(this.start, that.start) &&
            Objects.equals(midRef, that.midRef);

    }

    @Override
    public int hashCode() {
        // Include all relevant fields. This is safe because equals compares a subset of these
        // fields depending on the identity mode (midRef/type vs start), and including them here
        // ensures equal objects (per equals) will have equal hashCodes.
        return Objects.hash(channel, start, midRef);
    }

    @Override
    public String toString() {
        return channel.name() + ":" + (start != null ? start.atZone(Schedule.ZONE_ID).toLocalDateTime() : "<null>");
    }

    public String asString() {
        return channel.getXmlValue() + ":" + start + (StringUtils.isEmpty(midRef) ? "" : (":" + midRef));

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
    public int compareTo(@NonNull ScheduleEventIdentifier o) {
        if (this == o) {
            return 0;
        }

        // Compare by channel, then by start instant (nulls first), then by midRef (nulls first)
        return Comparator
            .comparing(ScheduleEventIdentifier::getStartInstant, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing((ScheduleEventIdentifier s) -> s.channel, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(s -> s.midRef, Comparator.nullsFirst(Comparator.naturalOrder()))
            .compare(this, o);

    }
}
