package nl.vpro.nep.service;

import nl.vpro.nep.domain.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPPlayerTokenService extends AutoCloseable {

    WideVineResponse widevineToken(String ip);

    PlayreadyResponse playreadyToken(String ip);

    FairplayResponse fairplayToken(String ip);

    String getPlayerTokenString();
}
