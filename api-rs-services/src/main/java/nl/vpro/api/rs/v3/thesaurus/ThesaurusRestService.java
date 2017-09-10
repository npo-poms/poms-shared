package nl.vpro.api.rs.v3.thesaurus;

import java.time.Instant;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.NoCache;

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


    /**
     * TODO too little similarity to {@link MediaRestService#changes}
     * Returned objects are different.
     */
    @GET
    @Path("/people/changes")
    @NoCache
    ThesaurusUpdates peopleUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to) throws Exception;

    @GET
    @Path("/items")
    ThesaurusResult listItems(@QueryParam("text") @DefaultValue("") String text,
            @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    /**
     * TODO too little similarity to {@link MediaRestService#changes}
     * Returned objects are different.
     */
    @GET
    @Path("/items/changes")
    @NoCache
    ThesaurusUpdates itemUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to) throws Exception;

    @GET
    @Path("/items/status")
    // TODO I think the return type can be proper. A 404 can be accomplished with some kind of NotFoundException.
    Response itemStatus(@QueryParam("id") String id) throws Exception;


}
