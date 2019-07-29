package nl.vpro.api.rs.v3.thesaurus;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.Constants;
import nl.vpro.domain.api.thesaurus.PersonResult;
import nl.vpro.domain.api.thesaurus.ThesaurusResult;
import nl.vpro.domain.gtaa.GTAAConcept;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

@SuppressWarnings("RestParamTypeInspection")
@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThesaurusRestService {
    String TAG = "thesaurus";
    String PATH = "/" + TAG;


    @GET
    @Path("/persons")
    PersonResult findPersons(
        @QueryParam("text") @DefaultValue("") String text,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max);


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/persons/updates")
    @NoCache
    InputStream listPersonUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
        );


    @GET
    @Path("/concepts")
    ThesaurusResult<GTAAConcept> listConcepts(
        @QueryParam("text") @DefaultValue("") String text,
        @QueryParam("schemes") List<String> schemes,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/concepts/updates")
    @NoCache
    InputStream listConceptUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    );

    @GET
    @Path("/concepts/status")
    @NoCache
    GTAAConcept conceptStatus(
        @QueryParam("id") String id
    );


}
