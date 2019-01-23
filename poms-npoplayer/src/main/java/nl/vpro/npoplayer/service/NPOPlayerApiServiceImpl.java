/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.npoplayer.service;

import javax.inject.Inject;

import nl.vpro.domain.npoplayer.NPOPlayerApiRequest;
import nl.vpro.domain.npoplayer.NPOPlayerApiResponse;
import nl.vpro.domain.npoplayer.NPOPlayerApiService;
import nl.vpro.npoplayer.NPOPlayerApiClient;

/**
 * @author r.jansen
 */
public class NPOPlayerApiServiceImpl implements NPOPlayerApiService {
    private final NPOPlayerApiClient client;

    @Inject
    public NPOPlayerApiServiceImpl(NPOPlayerApiClient client) {
        this.client = client;
    }

    @Override
    public NPOPlayerApiResponse request(String mid, NPOPlayerApiRequest request) {
        return client.getRestService().getVideo(mid, request);
    }
}
