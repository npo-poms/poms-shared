/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.Iterator;

import nl.vpro.domain.api.Change;
import nl.vpro.domain.api.Deletes;
import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.FilteringIterator;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface MediaRepository extends MediaLoader, Redirector {

    MediaResult list(Order order, long offset, Integer max);

    MediaResult listMembers(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max);

    MediaResult listDescendants(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max);

    ProgramResult listEpisodes(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max);

    @Deprecated
    Iterator<Change> changes(Long since, ProfileDefinition<MediaObject> current, ProfileDefinition<MediaObject> previous, Order order, Integer max, Long keepAlive);

    Iterator<Change> changes(Instant since, String mid, ProfileDefinition<MediaObject> current, ProfileDefinition<MediaObject> previous, Order order, Integer max, Long keepAlive, Deletes deletes);

    Iterator<MediaObject> iterate(ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max, FilteringIterator.KeepAlive keepAlive);


}
