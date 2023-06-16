/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.profile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.profile.Profile;
import nl.vpro.domain.api.profile.ProfileResult;
import nl.vpro.jmx.Description;

/**
 * Simple service to view of inspect the available site profiles.
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Path(ProfileRestService.PATH)
@Produces({MediaType.APPLICATION_XML})
@Description("Services on https://rs.poms.omroep.nl/v1/api" + ProfileRestService.PATH)
public interface ProfileRestService {

    String TAG  = "profiles";
    String PATH = "/" + TAG;

    String NAME = "name";


    /**
     * Returns a site profile by its key
     *
     * @param name an profile identifier
     * @return an existing profile or an error when no profile is found
     */
    @GET
    @Path("/{name}")
    Profile load(
        @PathParam(NAME) String name);

    @GET
    @Path("/list")
    ProfileResult list(
        @QueryParam("pattern") String pattern,
        @QueryParam("max") Integer max
    );


}
