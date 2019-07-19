package nl.vpro.api.rs.v3.thesaurus;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.Constants;
import nl.vpro.domain.api.thesaurus.PersonResult;
import nl.vpro.domain.api.thesaurus.ThesaurusResult;
import nl.vpro.domain.gtaa.ThesaurusObject;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

@SuppressWarnings("RestParamTypeInspection")
@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThesaurusRestService {
    String TAG = "thesaurus";
    String PATH = "/" + TAG;


    @GET
    @Path("/people")
    PersonResult findPersons(
        @QueryParam("text") @DefaultValue("") String text,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max);


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/people/updates")
    @NoCache
    InputStream listPersonUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
        );


    @GET
    @Path("/items")
    ThesaurusResult<ThesaurusObject> listItems(
        @QueryParam("text") @DefaultValue("") String text,
        @QueryParam("axes") @DefaultValue(("")) @NonNull List<String> axisList,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/items/updates")
    @NoCache
    InputStream listItemUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    );

    @GET
    @Path("/items/status")
    @NoCache
    ThesaurusObject itemStatus(
        @QueryParam("id") String id
    );


}
