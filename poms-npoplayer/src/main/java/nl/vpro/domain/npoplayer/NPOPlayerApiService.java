/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

/**
 * @author r.jansen
 * @since 5.10
 */
@Deprecated
public interface NPOPlayerApiService {

    NPOPlayerApiResponse request(String mid, NPOPlayerApiRequest request);
}
