package nl.vpro.rs.provider;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;

/**
 * Just to check what is in ES according to the publisher.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */

@Path(ApiProviderRestService.PATH)
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface ApiProviderRestService {
    String PATH = "/npoapi";


    @GET
    @Path("/page")
    Page getPage(
        @QueryParam("url") String url);

    @GET
    @Path("/media")
    MediaObject getMedia(
        @QueryParam("mid") String mid
    );


}
