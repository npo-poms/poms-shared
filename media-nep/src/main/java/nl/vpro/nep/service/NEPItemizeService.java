package nl.vpro.nep.service;

import java.io.OutputStream;
import java.time.*;

import org.apache.commons.lang3.time.DurationFormatUtils;

import nl.vpro.nep.domain.NEPItemizeResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPItemizeService {

    NEPItemizeResponse itemizeLive(String channel, Instant start, Instant stop, Integer max_bitrate);

    NEPItemizeResponse itemizeMid(String mid, Duration start, Duration stop, Integer max_bitrate);


    /**
     * NEP provides one service for two basicly different things.
     *
     * This grabs a frame from a MID, on a certain offset
     * @since 5.10
     */
    default void grabScreen(String mid, Duration offset, OutputStream outputStream) {
        String durationString = DurationFormatUtils.formatDuration(offset.toMillis(), "HH:mm:ss.SSS", true);
        grabScreen(mid, durationString, outputStream);
    }

    /**
     * NEP provides one service for two basicly different things.
     *
     * This grabs a frame from a live stream, on a certain instant in time
     * @since 5.10
     */
    default void grabScreen(String channel, Instant instant, OutputStream outputStream) {
        grabScreen(channel, instant.atZone(ZoneId.of("UTC")).toLocalDateTime().toString(), outputStream);
    }

    /**
     * Please use either {@link #grabScreen(String, Duration, OutputStream)} or {@link #grabScreen(String, Instant, OutputStream)}
     * @since 5.10
     */
    void grabScreen(String identifier, String time, OutputStream outputStream);

}
