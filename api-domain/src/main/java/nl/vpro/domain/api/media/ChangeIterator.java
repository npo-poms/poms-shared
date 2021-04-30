package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.CloseablePeekingIterator;
import nl.vpro.util.FilteringIterator;

/**
 * Wraps an existing Iterator of changes to allow for skipping certain values.
 *
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@Slf4j
public class ChangeIterator implements CloseablePeekingIterator<MediaChange> {

    private static final int LOG_BATCH_DEFAULT = 50000;

    private final int logBatch;
    private final Iterator<MediaChange> wrapped;

    private final ProfileDefinition<MediaObject> current;

    private final ProfileDefinition<MediaObject> previous;

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
    private long currentSkipCount = 0; // how many are skipped in e sequence now
    private final long keepAliveNulls; // after how many sequential skips a 'null' must be returned.

    /**
     * @param iterator      The original change feed (from ES)
     * @param since         The original since argument, which is sometimes needed during filtering
     *                      Sometimes the stream may contain objects sent before this.
     * @param current       The current profile as used by filtering
     * @param previous      The profile at the moment 'since'.
     * @param keepAliveNull (optional) If filtering will skip very many changes, the next() call may take several minutes.
     *                      If you set this to some finite positive value (it defaults to MAX_VALUE), every so many skips it wil return 'null'.
     *                      This null can be used to send something to the client to keep the connection alive.
     */
    public ChangeIterator(Iterator<MediaChange> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, Long keepAliveNull) {
        this(iterator, since, null, current, previous, keepAliveNull, null);
    }

    public ChangeIterator(Iterator<MediaChange> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous) {
        this(iterator, since, current, previous, null);
    }

    @Deprecated
    public ChangeIterator(Iterator<MediaChange> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, long keepAliveNull) {
        this(iterator, null, since, current, previous, keepAliveNull, null);
    }


    @lombok.Builder
    private ChangeIterator(Iterator<MediaChange> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, Long keepAliveNull, Integer logBatch) {
        this(iterator, since, null, current, previous, keepAliveNull, logBatch);

    }

    private  ChangeIterator(Iterator<MediaChange> iterator, Instant sinceDate, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, Long keepAliveNull, Integer logBatch) {
        this.wrapped = new FilteringIterator<>(iterator, Objects::nonNull);
        this.sinceDate = sinceDate;
        this.since = since;
        this.current = nullIsMatchAlways(current);
        this.previous = nullIsMatchAlways(previous);
        this.keepAliveNulls = keepAliveNull == null ? Long.MAX_VALUE : keepAliveNull;
        if (this.keepAliveNulls <= 0) {
            throw new IllegalArgumentException();
        }
        this.logBatch = logBatch == null ? LOG_BATCH_DEFAULT : logBatch;
    }


    @Deprecated
    public ChangeIterator(Iterator<MediaChange> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous) {
        this(iterator, since, current, previous, Long.MAX_VALUE);
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
        if (currentSkipCount >= keepAliveNulls) {
            currentSkipCount = 0;
            return null;
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
        needsFindNext = true;
        if (currentSkipCount >= keepAliveNulls) {
            currentSkipCount = 0;
            return null;
        }

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
            while (wrapped.hasNext()) {
                MediaChange n = wrapped.next();
                count++;
                sequence = n.getSequence();
                publishDate = n.getPublishDate();

                if (needsOutputAndAdapt(n)) {
                    next = nextnext;
                    nextnext = n;
                    if (next != null) {
                        currentSkipCount = 0;
                        break;
                    }
                } else {
                    if (n.isDeleted()) {
                        deletesSkipped++;
                    } else {
                        updatesSkipped++;
                    }
                    currentSkipCount++;
                    if (currentSkipCount >= keepAliveNulls) {
                        hasNext = true;
                        needsFindNext = false;
                        return;
                    }
                }
                if (count % logBatch == 0) {
                    log.info("{}: sequence: {} count: {}  skipped updates: {}, skipped deletes: {}", current.getName(), sequence, count, updatesSkipped, deletesSkipped);
                }
            }
            // handle last one
            if (next == null && nextnext != null) {
                next = nextnext;
                nextnext = null;
                if (count > logBatch) {
                    log.info("{}: sequence: {} count: {}  skipped updates: {}, skipped deletes: {}. Ready.", current.getName(), sequence, count, updatesSkipped, deletesSkipped);
                }
            }
            needsFindNext = false;
            hasNext = next != null;
        }
    }

    /**
     *
     */
    protected boolean needsOutputAndAdapt(MediaChange input) {
        if (input.isDeleted()) {
            return deleteNeedsOutput(input);
        } else {
            return updateNeedsOutput(input);
        }
    }

    /**
     * A delete needs output (always, of course _as_ a delete) if:
     *   - The associated 'since' timestamp is indeed after the configured one
     *   -
     */

    protected boolean deleteNeedsOutput(MediaChange input) {
        if (input.getMedia() == null ) {
            // No media, so we can't determine if it was in the previous profile
            // if it wasn't then no output needed
            // otherwise see below.
            return appliesToSince(input);
        }
        if (previous.test(input.getMedia())) {
            // we know it was in the previous profile
            // so we just need to issue a delete if this indeed was indeed deleted since then
            return appliesToSince(input);
        } else {
            // input was  not even in the previous profile, so it wasn't published.
            // no need to output a delete
            return false;
        }
    }

    /**
     * Whether an update needs to be outputted.
     * As a side effect the update may be converted to a <em>delete</em>
     */

    protected boolean updateNeedsOutput(MediaChange input) {
        final MediaObject media = input.getMedia();
        final boolean inCurrent  = current.test(media);
        final boolean inPrevious = previous.test(media);

        if (inCurrent) {
            // Is newer then since or did not apply under previous profile
            return appliesToSince(input) || !inPrevious;
        } else {
            // Lets see if it might be needed to issue a _delete_ in stead.

            if (publishedBeforeSince(input) && !inPrevious) {
                // Not previous too, so it wasn't published then, so we don't need an explicit delete
                return false;
            }
            // Return a delete since we can't determine whether the previous revision applied to the current
            // profile without extra document retrievals (NPA-134)
            input.setDeleted(true);
            return true;
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
        return new ProfileDefinition<>(new AbstractFilter<MediaObject>(null) {
        }); // matches everything
    }


}
