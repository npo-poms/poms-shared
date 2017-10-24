/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.rs.thesaurus.update;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.media.gtaa.GTAAPerson;

/**
 * @author Machiel Groeneveld
 * @since 5.5
 */

@Path(ThesaurusUpdateRestService.PATH)
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface ThesaurusUpdateRestService {

    String PATH = "/thesaurus/";
    String TAG = "thesaurus";

    @POST
    @Path("/person")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    GTAAPerson submitSigned(@NotNull NewPersonRequest request);




}
