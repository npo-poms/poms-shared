package nl.vpro.nep.service.impl;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.simple.Level;
import nl.vpro.nep.service.NEPSourcingService;

@Slf4j
@Named("NEPSourceServiceIngestService")
public  class NEPSourcingServiceImpl implements NEPSourcingService {

    private static final Jackson2Mapper MAPPER = Jackson2Mapper.getInstance();
    private final URI baseUrl;
    private final String bearerToken;
    private final String origin;
    private final List<Profile> profiles;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .build();

    public NEPSourcingServiceImpl(
        @Value("${nep.source-service.baseUrl:https://sourcingservice-acc.cdn1.usvc.nepworldwide.nl/v1/}") String baseUrl,
        @Value("${nep.source-service.bearerToken}") String bearerToken,
        @Value("${nep.sourcing-service-upload.host}") String ftpHost,
        @Value("${nep.sourcing-service-upload.username}") String username
        ) {
        this.baseUrl = URI.create(baseUrl);
        this.bearerToken = bearerToken;
        this.origin = username + "@" + ftpHost;
        this.profiles = List.of(
            new NEPSourcingService.Profile(
                UUID.fromString("89968391-875e-47d1-8912-7f2f5135a837"),
                List.of(
                    new NEPSourcingService.Parameter("drm", "true")
                )
            ));
    }

    @SneakyThrows
    @Override
    public CompletableFuture<HttpResponse<RequestResult>> ingest(Payload payload) {
        if (payload.origin() == null) {
            payload = payload.withOrigin(origin);
        }
        if (payload.profiles() == null) {
            payload = payload.withProfiles(profiles);
        }

        byte[] jsonBytes = Jackson2Mapper.getInstance().writeValueAsBytes(payload);
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofByteArray( MAPPER.writeValueAsBytes(payload)))
            .uri(baseUrl.resolve("ingest/request"))
            .header("Authorization", "Bearer " + bearerToken)
            .build();
        log.info("Sending ingest request to {}", httpRequest.uri());
        return httpClient.sendAsync(httpRequest,
            MAPPER.asBodyHandler(RequestResult.class, Level.INFO)
        );

    }
}


