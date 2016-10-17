package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Embeddable;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
public class ScheduleEventIdentifier implements Serializable {

    private Channel channel;

    protected Date start;

    public ScheduleEventIdentifier() {
        // to help hibernate
    }

    public ScheduleEventIdentifier(Channel channel, Instant start) {
        this.start = start == null ? null : Date.from(start);
        this.channel = channel;
    }


    @Deprecated
    public ScheduleEventIdentifier(Channel channel, Date start) {
        this(channel, start == null ? null : start.toInstant());
    }

    public Channel getChannel() {
        return channel;
    }

    public Instant getStartInstant() {
        return start == null ? null : start.toInstant();
    }


    public Date getStart() {
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
        if(start == null || that.start == null || !(start.getTime() == that.start.getTime())) {
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
        return super.toString() + ":" + channel + ":" + start;
    }


}
