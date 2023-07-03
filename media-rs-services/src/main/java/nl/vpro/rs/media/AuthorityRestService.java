package nl.vpro.rs.media;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;

import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.jmx.Description;

import static nl.vpro.rs.media.MediaBackendRestService.ERRORS;
import static nl.vpro.rs.media.MediaBackendRestService.ID;

/**
 * @author Michiel Meeuwissen
 * @since 7.7
 */
@Path("/authority")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Description("Services on https://api.poms.omroep.nl/authority")
public interface AuthorityRestService {

    @DELETE
    @Path("{id:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    Response deleteMedia(
        @Encoded @PathParam(ID) final String id,
        @QueryParam(ERRORS) String errors
    );


    @POST
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    Response update(
        @XopWithMultipartRelated MediaUpdate<?> update,
        @QueryParam(ERRORS) String errors
    );

}


