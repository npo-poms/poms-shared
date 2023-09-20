/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.npoplayer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.npoplayer.*;

/**
 * @author r.jansen
 * @since 5.10
 */
@Path("/npoplayer")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface NPOPlayerRestService {

    @POST
    @Path("/request")
    PlayerResponse forMid(PlayerRequest request);

    @POST
    @Path("/token")
    public PlayerResponse token(@Context HttpServletRequest request, TokenRequest tokenRequest);
}
