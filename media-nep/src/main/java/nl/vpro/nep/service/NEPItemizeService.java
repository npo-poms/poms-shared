package nl.vpro.nep.service;

import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiConsumer;

import nl.vpro.nep.domain.ItemizerStatusResponse;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.exception.NEPException;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPItemizeService extends  AutoCloseable {

    /**
     * NEP provides one service for two basicly different things.
     * <p>
     * This itemizes a media with a certain MID
     * @since 5.10
     */

    NEPItemizeResponse itemizeMid(String mid, Duration start, Duration stop, Integer max_bitrate) throws NEPException;

    /**
     * NEP provides one service for two basicly different things.
     * <p>
     * This grabs a frame from a MID, on a certain offset
     * @since 5.10
     */
    void grabScreenMid(String mid, Duration offset, BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException;


    /**
     * NEP provides one service for two basicly different things.
     * <p>
     * This itemizes a piece of a live stream
     * @since 5.10
     */
    NEPItemizeResponse itemizeLive(String channel, Instant start, Instant stop, Integer max_bitrate) throws NEPException;


    /**
     * NEP provides one service for two basicly different things.
     * <p>
     * This grabs a frame from a live stream, on a certain instant in time
     * @since 5.10
     */
    void grabScreenLive(String channel, Instant instant, BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException;

    String getLiveItemizerString();

    String getMidItemizerString();

    default String getItemizerString(Configuration configuration) {
        return switch (configuration) {
            case LIVE -> getLiveItemizerString();
            default -> getMidItemizerString();
        };
    }

    ItemizerStatusResponse getLiveItemizerJobStatus(String jobId);

    ItemizerStatusResponse getMidItemizerJobStatus(String jobId);

    default ItemizerStatusResponse getItemizerJobStatus(Configuration configuration, String jobId) {
        return switch (configuration) {
            case LIVE -> getLiveItemizerJobStatus(jobId);
            default -> getMidItemizerJobStatus(jobId);
        };
    }

    /**
     * Especially for testing we support two seperate configurations. One for itemizing of
     * 'live' or 'dvr' streams, and one for itemizing VOD or 'MID' streams.
     */
    enum Configuration {
        LIVE,
        MID
    }
}
