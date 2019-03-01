/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.media;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.*;
import nl.vpro.domain.media.MediaObject;

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
public interface MediaRestService {
    String TAG = "media";
    String PATH = "/" + TAG;

    String ID = "mid";
    String SORT = "sort";
    String SINCE = "since";
    String PUBLISHEDSINCE = "publishedSince";
    String CHECK_PROFILE = "checkProfile";
    String DELETES = "deletes";

    @GET
    @Path("/suggest")
    SuggestResult suggest(
        @QueryParam("input") @Size(min = 1) String input,
        @QueryParam(PROFILE) String profile,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    MediaResult list(
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @POST
    MediaSearchResult find(
        @Valid MediaForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @GET
    //@Path("/{mid : (?:(changes|multiple|redirects|iterate).+|(?!(changes|multiple|redirects|iterate)).*)}")
    @Path("/{mid}")
    MediaObject load(
        @PathParam(ID) String mid,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );


    @GET
    @Path("/redirects/")
    Response redirects(@Context Request request);

    @GET
    @Path("/multiple/")
    MultipleMediaResult loadMultiple(
        @QueryParam("ids") String mids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @POST
    @Path("/multiple/")
    MultipleMediaResult loadMultiple(
        IdList ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @GET
    @Path("/{mid}/members")
    MediaResult listMembers(
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @POST
    @Path("/{mid}/members")
    MediaSearchResult findMembers(
        @Valid MediaForm form,
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    /**
     * @param mid existing urn or mid
     */
    @GET
    @Path("/{mid}/episodes")
    ProgramResult listEpisodes(
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    /**
     * @param mid existing urn or mid
     */
    @POST
    @Path("/{mid}/episodes")
    ProgramSearchResult findEpisodes(
        @Valid MediaForm form,
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @GET
    @Path("/{mid}/descendants")
    MediaResult listDescendants(
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @POST
    @Path("/{mid}/descendants")
    MediaSearchResult findDescendants(
        @Valid MediaForm form,
        @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    @Path("/{mid:.*}/related")
    MediaResult listRelated(
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max,
        @QueryParam("partyId") String partyId

    );

    @POST
    @Path("/{mid:.*}/related")
    MediaSearchResult findRelated(
        @Valid MediaForm form,
        @Encoded @PathParam(ID) String mid,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max,
        @QueryParam("partyId") String partyId
    );

    @GET
    @Path("/changes/")
    @NoCache
    InputStream changes(
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SINCE) Long since,
        @QueryParam(PUBLISHEDSINCE) String publishedSince,
        @QueryParam(ORDER) @DefaultValue(ASC) String order,
        @QueryParam(MAX) Integer max,
        @QueryParam(CHECK_PROFILE) Boolean profileCheck,
        @QueryParam(DELETES) Deletes deletes,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) throws IOException;

    @POST
    @Path("/iterate/")
    @Deprecated
        //"This targets sitemaps, we'll make a sitemap feature on the page rest service"
    InputStream iterate(
        @Valid MediaForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    ) throws IOException;



}
