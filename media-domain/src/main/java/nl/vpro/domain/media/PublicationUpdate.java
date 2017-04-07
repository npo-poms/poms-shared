/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import nl.vpro.domain.media.support.Workflow;

/**
 *  Helper class for publication and revoke queue's.
 *  @author roekoe
 */
@Getter
public class PublicationUpdate implements Delayed, Serializable {
    private static final long serialVersionUID = 0L;



    public enum Action {
        PUBLISH,
        REVOKE
    }

    private final Long id;

    private final String mid;

    private final Instant time;

//    private final Date queueTime;

    private final String[] destinations;

    private final Action action;

    private final String reason;

    private final boolean republication;

    private final UUID transactionUUID;

    @Setter
    private PublicationUpdateConsumer consumer;


    public PublicationUpdate(Action action, String reason, String mid, Long id, Instant time, String[] destinations) {
        this(action, reason, mid, id, false, time, destinations);
    }

    public PublicationUpdate(Action publish, String mid, Long id, Instant time) {
        this(publish, "test", mid, id, false, time, null);
    }


    PublicationUpdate(Action action, String reason, String mid, Long id, boolean republication, Instant time, String[] destinations) {
        this.action = action;
        this.reason = reason;
        this.mid = mid;
        this.id = id;
        this.republication = republication;
        this.time = time != null ? time : Instant.now();
        this.destinations = destinations;
        this.transactionUUID = TransactionUUID.get();
    }

    public static PublicationUpdate publishToAll(String reason, MediaObject media) {
        return publish(reason, media, (String[]) null);
    }

    public static PublicationUpdate publish(String reason, MediaObject media, String... destinations) {
        return new PublicationUpdate(Action.PUBLISH, reason, media.getMid(), media.getId(), getPublishTime(media), destinations);
    }

    public static PublicationUpdate publish(String reason, Instant time, MediaObject media, String... destinations) {
        return new PublicationUpdate(Action.PUBLISH, reason, media.getMid(), media.getId(), time, destinations);
    }


    /**
     * Publish to <em>All</em> destinations. This is the normal action.
     */
    public static PublicationUpdate publishToAll(String reason, Instant time, MediaObject media) {
        return new PublicationUpdate(Action.PUBLISH, reason, media.getMid(), media.getId(), time, null);
    }


    public static PublicationUpdate revokeFromAll(String reason, MediaObject media) {
        return new PublicationUpdate(Action.REVOKE, reason, media.getMid(), media.getId(), media.getPublishStopInstant(), null);
    }

    public static PublicationUpdate revoke(String reason, MediaObject media, String... destinations) {
        return new PublicationUpdate(Action.REVOKE, reason, media.getMid(), media.getId(), media.getPublishStopInstant(), destinations);
    }

    public static PublicationUpdate republish(MediaObject media, String... destinations) {
        return new PublicationUpdate(Action.PUBLISH, "explicit_republish", media.getMid(), media.getId(), true, Instant.now(), destinations);
    }

    public static PublicationUpdate rerevoke(MediaObject media, String... destinations) {
        return new PublicationUpdate(Action.REVOKE, "explicit_republish", media.getMid(), media.getId(), true, Instant.now(), destinations);
    }

    public static PublicationUpdate create(String reason, MediaObject media) {
        if (media.isPublishable()) {
            return publishToAll(reason, media);
        } else {
            return revokeFromAll(reason, media);
        }
    }

    private static Instant getPublishTime(MediaObject media) {
        Instant publishTime = media.getPublishStartInstant();
        if (publishTime == null || publishTime.isBefore(media.getLastModifiedInstant())) {
            publishTime = media.getLastModifiedInstant();
        }
        return publishTime;
    }



    private static int getWorkFlowPriorityForPublication(Workflow w) {
        switch (w) {
            case PUBLISHED:
                return 1;
            default:
                return -1;
        }
    }

    private int getPriority() {
        return republication ? -1 : 1;
    }
    @Override
    public int compareTo(Delayed d) {
        if (d == null) return 1;
        if (d == this) {
            return 0;
        }
        long myDelay = this.getDelay(TimeUnit.MILLISECONDS);
        long theirDelay = d.getDelay(TimeUnit.MILLISECONDS);
        if (myDelay < 0L && theirDelay < 0L && d instanceof PublicationUpdate) {
            int diff = ((PublicationUpdate) d).getPriority() - getPriority();
            if (diff != 0) {
                return diff;
            }

        }
        long a = myDelay - theirDelay;
        if (a > 0L) {
            return 1;
        }
        if (a < 0L) {
            return -1;
        }
        return 0;
    }

    public UUID getTransactionUUID() {
        return transactionUUID;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (republication) {
            sb.append("RePublicationUpdate");
        } else {
            sb.append("PublicationUpdate");
        }
        sb.append("{");
        sb.append("action=").append(action);
        sb.append(", mid=").append(mid);
        sb.append(", id=").append(id);
        if(destinations != null) {
            sb.append(", destinations=").append(Arrays.asList(destinations).toString());
        }
        if(time != null) {
            sb.append(", time=").append(time);
        }
        if (reason != null) {
            sb.append(", reason=").append(reason);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        PublicationUpdate that = (PublicationUpdate)o;
        if(!id.equals(that.id)) {
            return false;
        }

        if(!action.equals(that.action)) {
            return false;
        }

        if(time == null) {
            return that.time == null;
        }
        return time.equals(that.time);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = (time != null) ? 31 * result + time.hashCode() : result;
        return result;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(getDelay(Instant.now()).toMillis(), TimeUnit.MILLISECONDS);
    }

    public Duration getDelay(Instant now) {
        return Duration.between(now, time);
    }


/*
    public long getTimeSinceCreation(TimeUnit timeUnit) {
        return -1 * timeUnit.convert(
            queueTime.getTime() - System.currentTimeMillis(),
            TimeUnit.MILLISECONDS);
    }

    */
/**
     * The earliest time that this object could come from the queue. That is the time it was put on, or some time later if it still had a positive delay then.
     *//*

    public Date getQueueTime() {
        return queueTime;
    }

*/
    public Duration getOverdue(Instant now) {
        return  getDelay(now).multipliedBy(-1);
    }


    public  interface PublicationUpdateConsumer extends Function<MediaObject, Boolean>, Serializable {

    }

}
