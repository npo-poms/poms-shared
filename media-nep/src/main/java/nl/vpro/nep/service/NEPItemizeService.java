package nl.vpro.nep.service;

import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import org.apache.commons.lang3.time.DurationFormatUtils;

import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPItemizeService {

    NEPItemizeResponse itemize(NEPItemizeRequest request);


    default void grabScreen(String mid, Duration offset, OutputStream outputStream) {
        String durationString = DurationFormatUtils.formatDuration(offset.toMillis(), "HH:mm:ss.SSS", true);
        grabScreen(mid, durationString, outputStream);
    }

    default void grabScreen(String channel, Instant instant, OutputStream outputStream) {
        grabScreen(channel, instant.atZone(ZoneId.of("UTC")).toLocalDateTime().toString(), outputStream);

    }

    void grabScreen(String identifier, String date, OutputStream outputStream);





}
