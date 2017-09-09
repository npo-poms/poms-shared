package nl.vpro.api.rs.v3.thesaurus;

import java.time.Instant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.vpro.domain.api.media.ThesaurusResult;
import nl.vpro.domain.api.media.ThesaurusUpdates;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThesaurusRestService {
    String TAG = "thesaurus";
    String PATH = "/" + TAG;

    @GET
    @Path("/people")
    ThesaurusResult listPeople(@QueryParam("text") @DefaultValue("") String text,
            @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Path("/people/updates")
    ThesaurusUpdates peopleUpdates(@QueryParam("from") Instant from, @QueryParam("to") Instant to) throws Exception;

    @GET
    @Path("/items")
    ThesaurusResult listItems(@QueryParam("text") @DefaultValue("") String text,
            @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Path("/items/updates")
    ThesaurusUpdates itemUpdates(@QueryParam("from") Instant from, @QueryParam("to") Instant to) throws Exception;

    @GET
    @Path("/items/status")
    Response itemStatus(@QueryParam("id") String id) throws Exception;


}
