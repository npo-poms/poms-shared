package nl.vpro.api.rs.v3.subtitles;

import java.util.Iterator;
import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.api.subtitles.SubtitlesSearchResult;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.domain.api.Constants.*;
import static nl.vpro.domain.api.Constants.DEFAULT_MAX_RESULTS_STRING;
import static nl.vpro.domain.api.Constants.MAX;


/**
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Path(SubtitlesRestService.PATH)
@Produces({VTT, EBU, SRT})// APPLICATION_JSON, APPLICATION_XML})
public interface SubtitlesRestService {

    String TAG = "subtitles";
    String PATH = "/" + TAG;

    @GET
    @Path("/{id}/{language}/{type}")
    Iterator<StandaloneCue> get(
        @PathParam("id") String mid,
        @PathParam("language") Locale locale,
        @PathParam("type") SubtitlesType type
    );

    @GET
    @Path("/{id}/{language}")
    Iterator<StandaloneCue> get(
        @PathParam("id") String mid,
        @PathParam("language") Locale locale);

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    SubtitlesSearchResult search(
        @Valid SubtitlesForm form,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

}
