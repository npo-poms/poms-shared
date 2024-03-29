package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.CloseablePeekingIterator;
import nl.vpro.util.FilteringIterator;

/**
 * Wraps an existing Iterator of changes to allow for skipping certain values
 * (this used to be implemented by setting them to {@code null}, but now happens by {@link MediaChange#setSkipped(boolean)})
 *
 * @author Michiel Meeuwissen
 * @since 6.2
 */
@Slf4j
public class MarkSkippedChangeIterator implements CloseablePeekingIterator<@NonNull MediaChange> {

    private static final int LOG_BATCH_DEFAULT = 50000;

    private final int logBatch;
    private final Iterator<MediaChange> wrapped;

    private final ProfileDefinition<MediaObject> profile;


    private final Instant sinceDate;
    private final Long since;

    private MediaChange next;
    private MediaChange nextnext;

    private boolean needsFindNext = true;
    private Boolean hasNext = null; // not known yet

    private Long sequence;
    private Instant publishDate;

    @Getter
    private long count = 0; // total number found until now
    @Getter
    private long updatesSkipped = 0; // how many are skipped in total now
    @Getter
    private long deletesSkipped = 0; // how many are skipped in total now
    private long currentSkipCount = 0; // how many are skipped in sequence now

    /**
     * @param iterator      The original change feed (from ES)
     * @param since         The original since argument, which is sometimes needed during filtering
     *                      Sometimes the stream may contain objects sent before this.
     * @param profile       The current profile as used by filtering
     */
    public MarkSkippedChangeIterator(Iterator<MediaChange> iterator, Instant since, final ProfileDefinition<MediaObject> profile) {
        this(iterator, since, profile, null);
    }


    @lombok.Builder
    private MarkSkippedChangeIterator(
        Iterator<@NonNull MediaChange> iterator,
        Instant since,
        final ProfileDefinition<MediaObject> profile,
        Integer logBatch) {
        this(iterator, since, null, profile, logBatch);
    }

    private MarkSkippedChangeIterator(
        Iterator<@NonNull MediaChange> iterator,
        Instant sinceDate,
        Long since,
        final ProfileDefinition<MediaObject> profile,
        Integer logBatch) {
        this.wrapped = new FilteringIterator<>(iterator, Objects::nonNull); // if incoming contains null already, ignore those
        this.sinceDate = sinceDate;
        this.since = since;
        this.profile = nullIsMatchAlways(profile);
        this.logBatch = logBatch == null ? LOG_BATCH_DEFAULT : logBatch;
    }

    @Override
    public boolean hasNext() {
        findNext();
        return hasNext;
    }

    @Override
    public MediaChange peek() {
        findNext();
        if (!hasNext) {
            throw new NoSuchElementException();
        }

        return next;
    }

    public Optional<MediaChange> peekNext() {
        findNext();
        return Optional.ofNullable(nextnext);
    }

    @Override
    public MediaChange next() {
        findNext();
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        assert next != null;
        needsFindNext = true;
        MediaChange result = next;
        next = null;
        return result;
    }

    /**
     * sequence number of most recent DocumentChange
     */
    public Long getSequence() {
        return sequence;
    }


    public Instant getPublishDate() {
        return publishDate;
    }

    @Override
    public void close() throws Exception {
        if (wrapped instanceof AutoCloseable) {
            ((AutoCloseable) wrapped).close();
        }
    }

    protected void findNext() {
        // we administrate the next change, but also the 'next next' one.
        // See NPA-105
        if (needsFindNext) {
            assert next == null;
            if (nextnext == null) {
                findNextNext();
            }
            next = nextnext;
            if (next != null) {
                findNextNext();
                count++;
                sequence = next.getSequence();
                publishDate = next.getPublishDate();
                markDeletedIfNotInProfile(next);
                // no
                if (next.isDeleted()) {
                    deletesSkipped++;
                } else {
                    updatesSkipped++;
                }
                if (! appliesToSince(next)) {
                    next.setSkipped(true);
                    currentSkipCount++;
                }
                if (count % logBatch == 0) {
                    log.info("{}: sequence: {} count: {}  skipped updates: {}, skipped deletes: {}",
                        profile.getName(),
                        sequence, count, updatesSkipped, deletesSkipped);
                }
                // handle last one
                //

                if (nextnext == null) {
                    if (count > logBatch) {
                        log.info("{}: sequence: {} count: {}  skipped updates: {}, skipped deletes: {}. Ready.", profile.getName(), sequence, count, updatesSkipped, deletesSkipped);
                    }
                }
            }
            needsFindNext = false;
            hasNext = next != null;
        }
    }
    protected void findNextNext() {
        nextnext = null;
        if (wrapped.hasNext()) { // only iterates more than once the first time.
            MediaChange n = wrapped.next();
            assert n != null;
            nextnext = n;
        }
    }


    /**
     * Whether an update needs to be outputted.
     * As a side effect the update may be converted to a <em>delete</em>
     */

    protected void markDeletedIfNotInProfile(MediaChange input) {
        final MediaObject media = input.getMedia();
        final boolean inProfile  = profile.test(media);


        if (! inProfile) {
            if (input.isDeleted()) {
                input.setSkipped(true);
            }
            // Let's see if it might be needed to issue a _delete_ in stead.
            // Return a delete since we can't determine whether the previous revision applied to the current
            // profile without extra document retrievals (NPA-134)
            input.setDeleted(true);

        }
    }

    /**
     * @return No 'since' or the given change was published after it.
     */
    protected boolean appliesToSince(MediaChange input) {
        if (! hasSince()) {
            return true;
        }
        if (since == null) {
            return sinceDate == null || input.getPublishDate() == null || !input.getPublishDate().isBefore(sinceDate);
        } else {
            return input.getSequence() >= since;
        }
    }

    /**
     *
     */
    protected boolean publishedBeforeSince(MediaChange input) {
        if (! hasSince()) {
            return false;
        }
        if (since == null) {
            return input.getPublishDate() == null || input.getPublishDate().isBefore(sinceDate);
        } else {
            return input.getSequence() < since;
        }
    }

    protected boolean hasSince() {
        return since != null || sinceDate != null;
    }

    @Override
    public void remove() {
        wrapped.remove();
    }

    private static ProfileDefinition<MediaObject> nullIsMatchAlways(ProfileDefinition<MediaObject> def) {
        if (def != null) {
            return def;
        }
        return new ProfileDefinition<>(new AbstractFilter<>(null) {
        }); // matches everything
    }


    public static class Builder {

        /**
         * @deprecated Used {@link #profile(ProfileDefinition)}.
         */
        @Deprecated
        public Builder current(ProfileDefinition<MediaObject> profile) {
            return profile(profile);
        }

    }

}
