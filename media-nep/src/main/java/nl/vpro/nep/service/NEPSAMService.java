package nl.vpro.nep.service;

import nl.vpro.nep.service.exception.NEPException;

import java.time.Duration;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService extends  AutoCloseable {

    String streamAccessLive(String channel, String ip, Duration duration) throws NEPException;
    String streamAccessMid(String mid, boolean drm, String ip, Duration duration) throws NEPException;

    String getStreamAccessLiveString();
    String getStreamAccessMidString();

}
