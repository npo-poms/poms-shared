package nl.vpro.rs.media;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;

import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.UpdateSupplier;

import static nl.vpro.rs.media.MediaBackendRestService.*;

/**
 * See <a href="https://jira.vpro.nl/browse/MSE-5484">MSE-5484</a>, <a href="https://publiekeomroep.atlassian.net/browse/P0MS-8">POMS-8</a>
 * <p>
 * Interface for the 'authority' services on <a href="https://api.poms.omroep.nl/authority">media backend</a>
 * @author Michiel Meeuwissen
 * @since 7.7
 */
@Path("/authority")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface AuthorityRestService {



    @DELETE
    @Path("{supplier}/{id:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    Response deleteMedia(
        @Encoded @PathParam("supplier") final UpdateSupplier supplier,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(ERRORS) String errors
    );


    @POST
    @Path("{supplier}")
    @Produces(MediaType.TEXT_PLAIN)
    Response update(
        @Encoded @PathParam("supplier") final UpdateSupplier supplier,
        @XopWithMultipartRelated MediaUpdate<?> update,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("true") Boolean validateInput
    );

}


