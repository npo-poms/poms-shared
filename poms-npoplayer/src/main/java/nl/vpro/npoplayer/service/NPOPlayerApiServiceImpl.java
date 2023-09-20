/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.npoplayer.service;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;

import nl.vpro.domain.npoplayer.*;
import nl.vpro.npoplayer.NPOPlayerApiClient;

/**
 * @author r.jansen
 */
@Named
@Slf4j
public class NPOPlayerApiServiceImpl implements NPOPlayerApiService {
    private final NPOPlayerApiClient client;

    @Inject
    public NPOPlayerApiServiceImpl(NPOPlayerApiClient client) {
        this.client = client;
        log.info("Created NPO Player Api Client: {}", client);
    }

    @Override
    public NPOPlayerApiResponse request(String mid, NPOPlayerApiRequest request) {
        return client.getRestService().getVideo(mid, request);
    }
}
