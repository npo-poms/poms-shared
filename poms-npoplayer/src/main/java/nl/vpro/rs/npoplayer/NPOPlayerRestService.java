/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.npoplayer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.npoplayer.*;

/**
 * This is a rest service definition which can be used to server for the NPO Player 8 and 9.
 * <p>
 * The player 8 stuff is derpecated and will soon e removed.
 * This service is e.g. implemented in <a href="https://rs.vpro.nl/v3/docs/api/#/npoplayer">vproapi</a>
 *
 * @author r.jansen
 * @since 5.10
 */
@Path("/npoplayer")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface NPOPlayerRestService {

    /**
     * player 8.
     * @deprecated This is player 8
     */
    @POST
    @Path("/request")
    @Deprecated
    PlayerResponse forMid(PlayerRequest request);

    /**
     * See <a href="https://docs.npoplayer.nl/implementation/create-a-jwt/">documentation of npo player 9</a>
     * player 9
     *<p>
     * Just performs the server side generation of the token. Implementation could user {@link nl.vpro.npoplayer.NpoPlayer9}.
     */
    @POST
    @Path("/token")
    TokenResponse token(TokenRequest tokenRequest);
}
