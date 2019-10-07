/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.rs.pages.update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.vpro.domain.page.update.PageUpdate;

/**
 * The Rest Interface is implemented by PageUpdateRestServiceImpl, but can also be used to generate clients.
 * @author Roelof Jan Koekoek
 * @since 3.0
 */

@Path(PageUpdateRestService.PATH)
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
public interface PageUpdateRestService {

    String PATH = "/pages/updates";

    @POST
    @Path("")
    Response save(
        @NotNull @Valid PageUpdate update
    );

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("")
    Response delete(
        @QueryParam("url") @NotNull String url,
        @QueryParam("batch")  Boolean batch,
        @QueryParam("max") Integer max,
        @QueryParam("wait") Boolean wait
    );

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("")
    PageUpdate load(
        @QueryParam("url") @NotNull String url,
        @QueryParam("loadDeleted") boolean loadDeleted

    );


}
