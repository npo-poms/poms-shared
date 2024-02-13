package nl.vpro.api.rs.v3.subtitles;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Locale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.api.subtitles.SubtitlesSearchResult;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.jmx.Description;

import static nl.vpro.api.rs.subtitles.Constants.*;
import static nl.vpro.domain.api.Constants.*;


/**
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Path(SubtitlesRestService.PATH)
@Produces({VTT, TT888, SRT, EBU})// APPLICATION_JSON, APPLICATION_XML})
@Description("Services on https://rs.poms.omroep.nl/v1/api" + SubtitlesRestService.PATH)
public interface SubtitlesRestService {

    String MID = "mid";
    String LANGUAGE = "language";
    String TYPE = "type";

    String TAG = "subtitles";
    String PATH = "/" + TAG;

    @GET
    @Path("/{mid}/{language}/{type}")
    Subtitles get(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale locale,
        @PathParam(TYPE) SubtitlesType type
    );

    @GET
    @Path("/{mid}/{language}")
    Subtitles get(
        @Encoded @PathParam(MID) String mid,
        @Schema(implementation = String.class, type = "string") @PathParam(LANGUAGE) Locale locale);


    @GET
    @Path("/{mid}")
    Subtitles get(
        @Encoded @PathParam(MID) String mid);

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    SubtitlesSearchResult search(
        @Valid SubtitlesForm form,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

}
