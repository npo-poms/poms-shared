package nl.vpro.nep.service.impl;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.domain.*;
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
      /*  RequestEntity<WideVineRequest> req = RequestEntity
            .post(URI.create(widevineUrl))
            .contentType(APPLICATION_JSON_UTF8)
            .body(body);
        return http.exchange(req, WideVineResponse.class).getBody();*/
        throw new NotImplementedException("Migrate from GUINEPController");

    }

    @Override
    public PlayreadyResponse playreadyToken(PlayreadyRequest request) {
      /*  PlayreadyRequest body = new PlayreadyRequest(ip(request), widevineKey);
        RequestEntity<PlayreadyRequest> req = RequestEntity
            .post(URI.create(playreadyUrl))
            .contentType(APPLICATION_JSON_UTF8)
            .body(body);
        ResponseEntity<PlayreadyResponse> exchange = http.exchange(req, PlayreadyResponse.class);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            return exchange.getBody();
        } else {

            throw new IllegalStateException(exchange.toString());
        }*/
        throw new NotImplementedException("Migrate from GUINEPController");

    }

    @Override
    public String streamUrl(StreamUrlRequest streamUrlRequest) {
/*
        StreamUrlRequest body = new StreamUrlRequest(ip, duration.getSeconds()));
        RequestEntity<StreamUrlRequest> req = RequestEntity
            .post(uri)
            .contentType(MediaType.parseMediaType("application/vnd.api+json"))
            .header(AUTHORIZATION, streamAccessKey)
            .body(body);
        try {
            ResponseEntity<StreamUrlResponse> response = http.exchange(req, StreamUrlResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                StreamUrlResponse entity = response.getBody();
                if (entity == null || entity.getData() == null) {
                    throw new IllegalStateException("No data found in " + response);
                }
                return entity.getData().getAttributes().getUrl();
            } else {
                log.error("For {} {} -> {}", uri, req, response);
                throw new IllegalStateException("Incorrect answer from " + uri + " " + response);
            }
        } catch (HttpClientErrorException httpClientErrorException) {
            logCurl(body, uri, httpClientErrorException);
            throw new IllegalStateException(httpClientErrorException.getResponseBodyAsString());
        } catch (Exception e) {
            String reqString = logCurl(body, uri, e);
            throw new IllegalStateException("Exception from NEP backend for POST  " + uri + " " + reqString + "' " + e.getClass().getName() + ": " + e.getMessage());
        }*/
        return "";
    }
}
