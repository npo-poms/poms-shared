package nl.vpro.nep.service;

import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.exception.NEPException;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPPlayerTokenService extends AutoCloseable {

    WideVineResponse widevineToken(String ip) throws NEPException;

    PlayreadyResponse playreadyToken(String ip) throws NEPException;

    FairplayResponse fairplayToken(String ip) throws NEPException;

    String getPlayerTokenString();
}
