package nl.vpro.rs.media;

import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.UpdateSupplier;
import nl.vpro.metis.IdClass;
import nl.vpro.validation.ValidationLevel;

import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
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

    String BROADCASTER_PATTERN = "^[A-Z0-9]{2,6}$";
    String VALIDATION_LEVEL = "validationLevel";

    String VALIDATION_LEVEL_DESCRIPTION = """
    The level of validation errors that will be fatal, and lead to bad requests.

    Validation is done always on every level, and be reported via response headers if (non fatal) errors are found.
    """;



    @DELETE
    @Path("{supplier}/{id:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    String deleteMedia(
        @PathParam("supplier") final UpdateSupplier supplier,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(ERRORS) String errors
    );


    @POST
    @Path("{supplier}")
    @Produces(MediaType.TEXT_PLAIN)
    String update(
        @PathParam("supplier") final UpdateSupplier supplier,
        @XopWithMultipartRelated MediaUpdate<?> update,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATION_LEVEL) @DefaultValue("WARNING") ValidationLevel level,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") boolean validateInput
    );

    @GET
    @Path("{supplier}/{id:.*}")
    @Produces({MediaType.APPLICATION_JSON, APPLICATION_XML})
    MediaUpdate<?> get(
        @PathParam("supplier") final UpdateSupplier supplier,
        @Encoded @PathParam(ID) final String id);


    /**
     * @since 7.10
     */
    @GET
    @Path("{class}/{supplier}/{broadcaster:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    String createMid(
        @PathParam("class") final IdClass idClass,
        @PathParam("supplier") final UpdateSupplier supplier,
        @Pattern(regexp =BROADCASTER_PATTERN) @PathParam("broadcaster") String broadcaster,
        @QueryParam("avType")AVType avType
    );


}


