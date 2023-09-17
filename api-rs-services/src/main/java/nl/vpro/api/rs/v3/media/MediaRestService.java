/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.media;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.*;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.jmx.Description;

import static nl.vpro.domain.api.Constants.*;

/**
 * <p>Endpoint which facilitates RPC like requests on media content. This API intents to capture meaningful and frequent
 * queries on media used when building a site or apps containing POMS media. This not a real REST API. It has no update
 * statements and it is mainly document oriented. Most calls will return a full media document and there are no separate
 * calls for sub-resources.</p>
 * <p>
 * The API returns three media instance pageTypes: Programs, Groups and Segments. A Program result always includes it's
 * contained Segments, but it is possible to retrieve Segments on there own. This is useful when a Segment occurs
 * on a playlist for example. </p>
 * <p>
 * Media id's may be either a full urn or a mid. Retrieval by crid is not implemented at this moment.</p>
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Path(MediaRestService.PATH)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Description("Services on https://rs.poms.omroep.nl/v1/api" + MediaRestService.PATH)
public interface MediaRestService {
    String TAG = "media";
    String PATH = "/" + TAG;

    String ID = "mid";
    String SORT = "sort";
    String SINCE = "since";
    String PUBLISHEDSINCE = "publishedSince";
    /**
     * @deprecated
     */
    @Deprecated
    String CHECK_PROFILE = "checkProfile";
    String DELETES = "deletes";
    String TAIL = "tail";
    String REASON_FILTER = "reasonFilter";

    @GET
    @Path("/suggest")
    SuggestResult suggest(
        @QueryParam("input") @Size(min = 1) String input,
        @QueryParam(PROFILE) String profile,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;


    /**
     * Lists a number of object directly from the API.
     * <p>
     * This only gives examples. It doesn't allow for any filtering, and is not fit for much data. See e.g. {@link #find(MediaForm, String, String, Long, Integer)} for a better use case.
     * <p>
     * If you need huge amount of data use {@link #iterate(MediaForm, String, String, Long, Integer)} or {@link #changes(String, String, Long, String, Order, Integer, Deletes, Tail, String)}.
     *
     * @param offset the first result. Note that this cannot be too big!
     */
    @GET
    MediaResult list(
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    );

    /**
     * Perform a search on the Media API.
     *
     */
    @POST
    MediaSearchResult find(
        @Valid MediaForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    @GET
    //@Path("/{mid : (?:(changes|multiple|redirects|iterate).+|(?!(changes|multiple|redirects|iterate)).*)}")
    @Path("/{mid:.*}")
    MediaObject load(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    ) throws ProfileNotFoundException;


    @GET
    @Path("/redirects/")
    Response redirects(@Context Request request);

    @GET
    @Path("/multiple/")
    MultipleMediaResult loadMultiple(
        @QueryParam("ids") String mids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    ) throws ProfileNotFoundException;

    @POST
    @Path("/multiple/")
    MultipleMediaResult loadMultiple(
        IdList ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    ) throws ProfileNotFoundException;

    @GET
    @Path("/{mid:.*}/members")
    MediaResult listMembers(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    @POST
    @Path("/{mid:.*}/members")
    MediaSearchResult findMembers(
        @Valid MediaForm form,
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    /**
     * @param mid existing urn or mid
     */
    @GET
    @Path("/{mid:.*}/episodes")
    ProgramResult listEpisodes(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    /**
     * @param mid existing urn or mid
     */
    @POST
    @Path("/{mid:.*}/episodes")
    ProgramSearchResult findEpisodes(
        @Valid MediaForm form,
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    @GET
    @Path("/{mid:.*}/descendants")
    MediaResult listDescendants(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    @POST
    @Path("/{mid:.*}/descendants")
    MediaSearchResult findDescendants(
        @Valid MediaForm form,
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max
    ) throws ProfileNotFoundException;

    @GET
    @Path("/{mid:.*}/related")
    MediaResult listRelated(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max,
        @QueryParam("partyId") String partyId

    ) throws ProfileNotFoundException;

    @POST
    @Path("/{mid:.*}/related")
    MediaSearchResult findRelated(
        @Valid MediaForm form,
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) @Max(Constants.MAX_RESULTS) Integer max,
        @QueryParam("partyId") String partyId
    ) throws ProfileNotFoundException;

    @GET
    @Path("/changes/")
    @NoCache
    @Produces({MediaType.APPLICATION_JSON})
    Response changes(
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SINCE) Long since,
        @QueryParam(PUBLISHEDSINCE) String publishedSince,
        @QueryParam(ORDER) @DefaultValue(ASC)  Order order,
        @QueryParam(MAX) Integer max,
        @QueryParam(DELETES) Deletes deletes,
        @QueryParam(TAIL) Tail tail,
        @QueryParam(REASON_FILTER) String reasonFilter
    ) throws ProfileNotFoundException;


    /**
     * Returns all data of a certain profile
     * <p>
     *  This can be used to make sitemaps, we might  make a sitemap feature on the page rest service too.
     */
    @POST
    @Path("/iterate/")
    @NoCache
    @Produces({MediaType.APPLICATION_JSON})
    Response iterate(
        @Valid MediaForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    ) throws ProfileNotFoundException;



}
