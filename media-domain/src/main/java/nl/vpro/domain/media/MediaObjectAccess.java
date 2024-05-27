package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.annotations.Beta;

import nl.vpro.domain.media.support.*;

/**
 *
 * Makes some package local method accessible.
 * <p>
 * This should normally only be done by media backend processes.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Slf4j
public class MediaObjectAccess {

    public static void setRepubDate(MediaObject object, Instant repubDate) {
        object.setRepubDate(repubDate);
    }

    public static void setWorkflow(MediaObject object, Workflow workflow) {
        setWorkflow(object, workflow, null);
    }


    public static void setWorkflow(@NonNull MediaObject object, Workflow workflow, String reason) {
        PublishableObjectAccess.setWorkflow(object, workflow);
        MediaObjects.appendReason(object, reason);
        object.setRepubDestinations(null);
    }

    public static void setStreamingPlatformStatus(MediaObject object, StreamingStatusImpl status) {
        if (status != object.getStreamingPlatformStatus()) {
            log.info("Set streaming platform status for {} from {} to {}", object, object.getStreamingPlatformStatus(), status);
            object.setStreamingPlatformStatus(status);
        }
    }
    public static StreamingStatusImpl getModifiableStreamingPlatformStatus(MediaObject object) {
        return object.getModifiableStreamingPlatformStatus();
    }

    public static void setAuthorizedDuration(MediaObject o, Duration duration) {
        if (o != null) {
            AuthorizedDuration authorized = AuthorizedDuration.authorized(duration);
            o.setDuration(authorized);
        } else {
            log.warn("Can't set duration {} because not mediaobject given", duration);
        }

    }
    public static void setMid(MediaObject o, String mid) {
        o.mid = mid;
    }

    public static void setExternalVersion(MediaObject o , String v) {
        o.externalVersion = v;
    }

    public static void setPdAuthorityImported(Program p, boolean pdAuthorityImported) {
        p.setPdAuthorityImported(pdAuthorityImported);;
    }

    public static Boolean isPdAuthorityImported(Program p) {
        return p.getPdAuthorityImported();
    }

    public static String getCurrentCorrelationId(MediaObject m) {
        return m.correlationId;
    }


    @Beta
    public  static void setCorrelationForSegmentDefault(boolean defaultCorrelation) {
        Segment.defaultCorrelation = defaultCorrelation;
    }

    @Beta
    public  static boolean getCorrelationForSegmentDefault() {
        return Segment.defaultCorrelation;
    }






}
