package nl.vpro.api.rs.v3.thesaurus;

import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.media.ThesaurusResult;
import nl.vpro.domain.api.media.ThesaurusUpdates;
import nl.vpro.domain.media.gtaa.GTAAPerson;

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
