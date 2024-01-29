package nl.vpro.rs.media;

import java.io.InputStream;
import java.time.Duration;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static nl.vpro.rs.media.MediaBackendRestService.ERRORS;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Path("/frames")
public interface FrameCreatorRestService {


    @PUT
    @Path("/{mid}/{offset}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "image/*"})
    Response createFrame(
        @PathParam("mid") String mid,
        @PathParam("offset") Duration duration,
        @QueryParam(ERRORS) String errors,
        @HeaderParam("Content-Transfer-Encoding") String transferEncoding,
        InputStream stream
    );
}
