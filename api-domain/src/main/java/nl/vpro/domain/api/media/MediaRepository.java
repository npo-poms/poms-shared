/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.functional.ReasonedPredicate;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.media.MediaLoader;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.CloseableIterator;
import nl.vpro.util.FilteringIterator;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface MediaRepository extends MediaLoader, Redirector {

    MediaResult list(Order order, long offset, Integer max);

    MediaResult listMembers(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max) throws ProfileNotFoundException;

    MediaResult listDescendants(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max) throws ProfileNotFoundException;

    ProgramResult listEpisodes(MediaObject media, ProfileDefinition<MediaObject> profile, Order order, long offset, Integer max) throws ProfileNotFoundException;

    @Deprecated
    CloseableIterator<MediaChange> changes(Long since, ProfileDefinition<MediaObject> current, ProfileDefinition<MediaObject> previous, Order order, Integer max, Long keepAlive);

    CloseableIterator<MediaChange> changes(
        @Nullable final Instant since,
        @Nullable final String mid,
        @Nullable final ProfileDefinition<MediaObject> currentProfile,
        @Nullable final Order order,
        @Nullable final Integer max,
        @Nullable Deletes deletes,
        @Nullable final Tail tail,
        @Nullable final ReasonedPredicate<MediaChange> filter
    );

    CloseableIterator<MediaObject> iterate(ProfileDefinition<MediaObject> profile, MediaForm form, long offset, Integer max, FilteringIterator.KeepAlive keepAlive);


}
