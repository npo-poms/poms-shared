package nl.vpro.api.rs.v3.thesaurus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.media.ThesaurusResult;
import nl.vpro.domain.api.media.ThesaurusUpdates;
import nl.vpro.domain.media.gtaa.GTAAPerson;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThesaurusRestService {
    String TAG = "thesaurus";
    String PATH = "/" + TAG;

    @GET
    @Path("/people")
    ThesaurusResult list(@QueryParam("text") @DefaultValue("") String text,
            @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Path("/people/updates")
    ThesaurusUpdates updates(@QueryParam("from") String from, @QueryParam("to") String to) throws Exception;

    @POST
    @Path("/person")
    GTAAPerson submitSigned(String jwt);

}
