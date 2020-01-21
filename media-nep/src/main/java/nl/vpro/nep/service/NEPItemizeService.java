package nl.vpro.nep.service;

import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

import nl.vpro.nep.domain.NEPItemizeResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPItemizeService {

    /**
     * NEP provides one service for two basicly different things.
     *
     * This itemizes a media with a certain MID
     * @since 5.10
     */

    NEPItemizeResponse itemize(String mid, Duration start, Duration stop, Integer max_bitrate);

    /**
     * NEP provides one service for two basicly different things.
     *
     * This grabs a frame from a MID, on a certain offset
     * @since 5.10
     */
    void grabScreen(String mid, Duration offset, OutputStream outputStream);


    /**
     * NEP provides one service for two basicly different things.
     *
     * This itemizes a piece of a live stream
     * @since 5.10
     */
    NEPItemizeResponse itemize(String channel, Instant start, Instant stop, Integer max_bitrate);


    /**
     * NEP provides one service for two basicly different things.
     *
     * This grabs a frame from a live stream, on a certain instant in time
     * @since 5.10
     */
    void grabScreen(String channel, Instant instant, OutputStream outputStream);

}
