package nl.vpro.nep.service;

import nl.vpro.nep.domain.*;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService {


    WideVineResponse widevineToken(WideVineRequest request);

    PlayreadyResponse playreadyToken(PlayreadyRequest request);

    String streamUrl(String streamId, StreamUrlRequest streamUrlRequest);


}
