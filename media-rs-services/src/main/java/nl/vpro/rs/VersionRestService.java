package nl.vpro.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
@Path("/version")
@Produces(MediaType.TEXT_PLAIN)
public interface VersionRestService {


    @GET
    String getVersion();
}
