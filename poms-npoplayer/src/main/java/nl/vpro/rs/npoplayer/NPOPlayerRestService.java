/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.npoplayer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.npoplayer.*;

/**
 * This is a wrapper around the NPO Player 8 and 9 API.
 * @author r.jansen
 * @since 5.10
 */
@Path("/npoplayer")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface NPOPlayerRestService {

    /**
     * player 8.
     */
    @POST
    @Path("/request")
    @Deprecated
    PlayerResponse forMid(PlayerRequest request);

    /**
     * See <a href="https://docs.npoplayer.nl/implementation/create-a-jwt/">documentation of npo player 9</a>
     * player 9
     */
    @POST
    @Path("/token")
    TokenResponse token(TokenRequest tokenRequest);
}
