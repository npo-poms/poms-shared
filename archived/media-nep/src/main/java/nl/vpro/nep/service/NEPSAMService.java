package nl.vpro.nep.service;

import java.time.Duration;
import java.util.Optional;

import nl.vpro.nep.service.exception.NEPException;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService extends  AutoCloseable {

    Optional<String> streamAccessLive(String channel, String ip, Duration duration) throws NEPException;
    Optional<String> streamAccessMid(String mid, boolean drm, String ip, Duration duration) throws NEPException;

    String getStreamAccessLiveString();
    String getStreamAccessMidString();

}
