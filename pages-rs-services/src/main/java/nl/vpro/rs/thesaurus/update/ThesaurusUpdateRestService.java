/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.rs.thesaurus.update;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.gtaa.GTAANewPerson;
import nl.vpro.domain.gtaa.GTAANewGenericConcept;
import nl.vpro.domain.gtaa.GTAAPerson;
import nl.vpro.domain.gtaa.GTAAConcept;

/**
 * @author Machiel Groeneveld
 * @since 5.5
 */

@Path(ThesaurusUpdateRestService.PATH)
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface ThesaurusUpdateRestService {

    String AUTHENTICATION_SCHEME = "Bearer";
    String TAG = "thesaurus";
    String PATH = "/" + TAG;

    @POST
    @Path("/person")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    GTAAPerson submit(
        @NotNull @HeaderParam(HttpHeaders.AUTHORIZATION) String jws,
        @NotNull GTAANewPerson person
    );

    @POST
    @Path("/concept")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    GTAAConcept submit(
        @NotNull@HeaderParam(HttpHeaders.AUTHORIZATION) String jws,
        @NotNull GTAANewGenericConcept thesaurusObject
    );

}
