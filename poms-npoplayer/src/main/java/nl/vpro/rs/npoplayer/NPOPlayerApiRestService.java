/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.npoplayer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.npoplayer.NPOPlayerApiRequest;
import nl.vpro.domain.npoplayer.NPOPlayerApiResponse;

/**
 * @author r.jansen
 * @since 5.10
 */
@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Deprecated
public interface NPOPlayerApiRestService {

    @POST
    @Path("/video/{mid}/initjs")
    NPOPlayerApiResponse getVideoWithTopspin(
        @Encoded @PathParam("mid") String mid, NPOPlayerApiRequest request);

    @POST
    @Path("/video/{mid}/init")
    NPOPlayerApiResponse getVideo(
        @Encoded @PathParam("mid") String mid, NPOPlayerApiRequest request);
}
