package nl.vpro.nep.service;

import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPPlayerTokenService {


    WideVineResponse widevineToken(String ip);

    PlayreadyResponse playreadyToken(String ip);

}
