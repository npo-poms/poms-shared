/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.page;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.page.*;
import nl.vpro.jmx.Description;

import static nl.vpro.domain.api.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Path(PageRestService.PATH)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Description("Services on https://rs.poms.omroep.nl/v1/api" + PageRestService.PATH)
public interface PageRestService {
    String TAG = "pages";
    String PATH = "/" + TAG;

    @GET
    @Path("/suggest")
    SuggestResult suggest(
        @QueryParam("input") @Size(min = 1) String input,
        @QueryParam(PROFILE) String profile,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    ) throws ProfileNotFoundException;

    @GET
    PageResult list(
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    ) throws ProfileNotFoundException;

    @POST
    PageSearchResult find(
        @Valid PageForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max) throws ProfileNotFoundException;

    @GET
    @Path("/multiple")
    MultiplePageResult loadMultiple(
        @QueryParam("ids") String ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @POST
    @Path("/multiple")
    MultiplePageResult loadMultiple(
        IdList ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @GET
    @Path("/related")
    PageResult listRelated(
        @QueryParam("id") String id,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    ) throws ProfileNotFoundException;

    @POST
    @Path("/related")
    PageSearchResult findRelated(
        @Valid PageForm form,
        @QueryParam("id") String id,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    ) throws ProfileNotFoundException;


    @POST
    @Path("/iterate/")
    @Deprecated
        //"This targets sitemaps, we'll make a sitemap feature on the page rest service"
    @NoCache
    Response iterate(
        @Valid PageForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max
    );
}
