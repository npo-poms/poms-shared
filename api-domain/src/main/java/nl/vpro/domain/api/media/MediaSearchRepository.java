/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface MediaSearchRepository extends MediaLoader, MediaRedirector, MediaRepository {

    MediaSearchResult find(ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max);

    MediaSearchResult findMembers(MediaObject media, ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max);

    ProgramSearchResult findEpisodes(MediaObject media, ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max);

    MediaSearchResult findDescendants(MediaObject media, ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max);

    MediaSearchResult findRelated(MediaObject media, ProfileDefinition<MediaObject> profile, MediaForm form, Integer max);


}
