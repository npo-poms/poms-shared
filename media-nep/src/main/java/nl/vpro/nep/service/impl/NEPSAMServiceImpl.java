package nl.vpro.nep.service.impl;

import java.time.Duration;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.domain.PlayreadyRequest;
import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineRequest;
import nl.vpro.nep.domain.WideVineResponse;
import nl.vpro.nep.service.NEPSAMService;

/**
 * https://jira.vpro.nl/browse/MSE-3754
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPSAMService")
public class NEPSAMServiceImpl implements NEPSAMService {


    final Supplier<String> authenticator;

    final String baseUrl;


    @Inject
    public NEPSAMServiceImpl(
        @Value("${nep.sam.baseUrl}") @Nonnull String baseUrl,
        @Named("NEPSAMAuthenticator") @Nonnull Supplier<String> authenticator) {
        this.authenticator = authenticator;
        this.baseUrl = baseUrl;
    }


    @Override
    public WideVineResponse widevineToken(WideVineRequest request) {
        throw new NotImplementedException("Migrate from GUINEPController");

    }

    @Override
    public PlayreadyResponse playreadyToken(PlayreadyRequest request) {
        throw new NotImplementedException("Migrate from GUINEPController");

    }

    @Override
    public String streamUrl(String mid, Duration duration) {
        throw new NotImplementedException("Migrate from GUINEPController");
    }
}
