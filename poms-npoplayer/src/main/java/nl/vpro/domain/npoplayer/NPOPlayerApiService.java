/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

/**
 * @author r.jansen
 */
public interface NPOPlayerApiService {

    NPOPlayerApiResponse request(String mid, NPOPlayerApiRequest request);
}
