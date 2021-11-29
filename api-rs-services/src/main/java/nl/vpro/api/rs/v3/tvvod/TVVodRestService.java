/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.tvvod;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.media.MediaTable;
import nl.vpro.jmx.Description;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
@Path(TVVodRestService.PATH)
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Description("Services on https://rs.poms.omroep.nl/v1/api" + TVVodRestService.PATH)
public interface TVVodRestService {
    String TAG  = "tvvod";
    String PATH = "/" + TAG;


    @GET
    @Path("/{mid}")
    MediaTable get(
        @Encoded  @PathParam("mid") @Size(min = 1) String mid
    );

}
