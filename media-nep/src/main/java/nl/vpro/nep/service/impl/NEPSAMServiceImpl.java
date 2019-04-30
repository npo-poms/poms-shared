package nl.vpro.nep.service.impl;

import javax.inject.Named;

import org.apache.commons.lang3.NotImplementedException;

import nl.vpro.nep.domain.PlayreadyRequest;
import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineRequest;
import nl.vpro.nep.domain.WideVineResponse;
import nl.vpro.nep.service.NEPSAMService;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPSAMService")
public class NEPSAMServiceImpl implements NEPSAMService {
    @Override
    public WideVineResponse widevineToken(WideVineRequest request) {
        throw new NotImplementedException("Migrate from GUINEPController");

    }

    @Override
    public PlayreadyResponse playreadyToken(PlayreadyRequest request) {
        throw new NotImplementedException("Migrate from GUINEPController");
    }
}
