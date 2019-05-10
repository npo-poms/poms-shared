package nl.vpro.nep.service;

import java.time.Duration;

import nl.vpro.nep.domain.PlayreadyRequest;
import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineRequest;
import nl.vpro.nep.domain.WideVineResponse;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService {


    WideVineResponse widevineToken(WideVineRequest request);

    PlayreadyResponse playreadyToken(PlayreadyRequest request);

    String streamUrl(String mid, Duration duration);


}
