package nl.vpro.nep.service.impl;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.*;
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

    private final HttpClient httpClient = HttpClient.newBuilder()
        .build();

    public NEPSourcingServiceImpl(
        @Value("${nep.source-service.baseUrl:https://sourcingservice-acc.cdn1.usvc.nepworldwide.nl/v1/}") String baseUrl,
        @Value("${nep.source-service.bearerToken}") String bearerToken) {
        this.baseUrl = URI.create(baseUrl);
        this.bearerToken = bearerToken;
    }

    @SneakyThrows
    @Override
    public CompletableFuture<HttpResponse<RequestResult>> ingest(Payload payload) {

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


