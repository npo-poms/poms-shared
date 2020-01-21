package nl.vpro.nep.service;

import java.time.Duration;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService {

    String streamAccessLive(String channel, String ip, Duration duration);
    String streamAccessMid(String mid, boolean drm, String ip, Duration duration);



}
