/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.pages.update;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import nl.vpro.domain.page.PageIdMatch;
import nl.vpro.domain.page.update.PageUpdate;
import nl.vpro.jmx.Description;

/**
 * The Rest Interface is implemented by PageUpdateRestServiceImpl, but can also be used to generate clients.
 * @author Roelof Jan Koekoek
 * @since 3.0
 */

@Path(PageUpdateRestService.PATH)
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
@Description("Services on https://publish.pages.omroep.nl" + PageUpdateRestService.PATH)
public interface PageUpdateRestService {

    String PATH = "/pages/updates";

    String WAIT = "wait";
    String MATCH = "match";

    String URL = "url";


    @POST
    @Path("")
    Response save(
        @NotNull @Valid PageUpdate update,
        @QueryParam(WAIT) Boolean wait
    );

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("")
    Response delete(
        @QueryParam("url") @NotNull String url,
        @QueryParam("batch")  Boolean batch,
        @QueryParam("max") Integer max,
        @QueryParam(WAIT) Boolean wait,
        @QueryParam(MATCH) @DefaultValue("BOTH") PageIdMatch match,
        @Context HttpHeaders httpHeaders,
        @Context HttpServletResponse response
    );

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("")
    PageUpdate load(
        @QueryParam(URL) @NotNull String url,
        @QueryParam("loadDeleted") boolean loadDeleted,
        @QueryParam(MATCH) @DefaultValue("BOTH") PageIdMatch match
    );



}
