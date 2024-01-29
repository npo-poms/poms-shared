package nl.vpro.api.rs.v3.thesaurus;

import java.time.Instant;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.Constants;
import nl.vpro.domain.api.thesaurus.PersonResult;
import nl.vpro.domain.api.thesaurus.ThesaurusResult;
import nl.vpro.domain.gtaa.GTAAConcept;
import nl.vpro.domain.gtaa.Scheme;
import nl.vpro.jmx.Description;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Description("Services on https://rs.poms.omroep.nl/v1/api" + ThesaurusRestService.PATH)
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
    Response listPersonUpdates(
        @QueryParam("from") @NotNull Instant from,
        @QueryParam("to") Instant to
    );


    @GET
    @Path("/concepts")
    ThesaurusResult<GTAAConcept> listConcepts(
        @QueryParam("text") @DefaultValue("") String text,
        @QueryParam("schemes") Set<Scheme> schemes,
        @QueryParam("not") @DefaultValue("false")  boolean not,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/concepts/updates")
    @NoCache
    Response listConceptUpdates(
        @QueryParam("from")  @NotNull Instant from,
        @QueryParam("to") Instant to
    );

    @GET
    @Path("/concepts/status")
    @NoCache
    GTAAConcept conceptStatus(
        @QueryParam("id") String id
    );


}
