/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.media.*;
import nl.vpro.util.CloseableIterator;
import nl.vpro.util.FilteringIterator;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface MediaService extends MediaProvider {

    SuggestResult suggest(String input, String profile, Integer max) throws ProfileNotFoundException;

    CloseableIterator<MediaChange> changes(
        String profile,
        boolean profileCheck,
        Instant since,
        String mid,
        Order order,
        Integer max,
        Long keepAlive,
        boolean withSequences,
        Deletes deletes,
        Tail tails,
        Predicate<MediaChange> reasonFilter) throws ProfileNotFoundException;

    @Override
    <T extends MediaObject> T findByMid(boolean loadDeleted, String mid);

    List<MediaObject> loadAll(List<String> ids);

    RedirectList redirects();

    MediaResult list(Order order, Long offset, Integer max);

    CloseableIterator<MediaObject> iterate(String profile, MediaForm form, Long offset, Integer max, FilteringIterator.KeepAlive keepAlive) throws ProfileNotFoundException;

    MediaSearchResult find(String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaResult listMembers(MediaObject media, String profile, Order order, Long offset, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findMembers(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    ProgramResult listEpisodes(MediaObject media, String profile, Order order, Long offset, Integer max) throws ProfileNotFoundException;

    ProgramSearchResult findEpisodes(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaResult listDescendants(MediaObject media, String profile, Order order, Long offset, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findDescendants(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findRelated(MediaObject media, String profile, MediaForm form, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findRelatedInTopspin(MediaObject media, String profile, MediaForm form, Integer max, String partyId, String clazz) throws ProfileNotFoundException;

    MediaType getType(String id);

    Optional<String> redirect(String mid);


}
