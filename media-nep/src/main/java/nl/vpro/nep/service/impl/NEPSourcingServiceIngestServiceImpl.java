package nl.vpro.nep.service.impl;


import lombok.extern.slf4j.Slf4j;

import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.NEPSourceServiceIngestService;

@Slf4j
@Named("NEPSourceServiceIngestService")
public  class NEPSourcingServiceIngestServiceImpl implements NEPSourceServiceIngestService {

    private final String baseUrl;
    private final String bearerToken;

    public NEPSourcingServiceIngestServiceImpl(
        @Value("${nep.source-service.baseUrl:https://sourcingservice-acc.cdn1.usvc.nepworldwide.nl/v1/ingest/}") String baseUrl,
        @Value("${nep.source-service.bearerToken}") String bearerToken) {
        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
    }

    @Override
    public void ingest(Payload payload) {


    }
}


