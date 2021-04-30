package nl.vpro.domain.api.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.CloseableIterator;
import nl.vpro.util.FilteringIterator;

/**
 * Wraps an existing Iterator of changes to allow for skipping certain values.
 *
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@Slf4j
public class ChangeIterator implements CloseableIterator<MediaChange> {

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

    private Long sequence; // sequence number of most recent DocumentChange (whether filtered or not)
    private Instant publishDate;

    private long count = 0; // total number found until now
    private long updatesSkipped = 0; // how many are skipped in total now
    private long deletesSkipped = 0; // how many are skipped in total now
    private long currentSkipCount = 0; // how many are skipped in e sequence now
    private final long keepAliveNulls; // after how many sequential skips a 'null' must be returned.

    /**
     * @param iterator      The original change feed (from ES)
     * @param since         The since argument, which is sometimes needed during filtering
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

    public ChangeIterator(Iterator<MediaChange> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, long keepAliveNull) {
        this(iterator, null, since, current, previous, keepAliveNull, null);
    }


    @lombok.Builder
    public ChangeIterator(Iterator<MediaChange> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, Long keepAliveNull, Integer logBatch) {
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


    public ChangeIterator(Iterator<MediaChange> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous) {
        this(iterator, since, current, previous, Long.MAX_VALUE);
    }

    @Override
    public boolean hasNext() {
        findNext();
        return hasNext;

    }

    @Override
    public MediaChange next() {
        findNext();
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        needsFindNext = true;
        if (currentSkipCount > keepAliveNulls) {
            currentSkipCount = 0;
            return null;
        }
        MediaChange result = next;
        next = null;
        return result;
    }

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
                boolean isDelete = n.isDeleted();
                if (needsOutputAndAdapt(n)) {
                    next = nextnext;
                    nextnext = n;
                    if (next != null) {
                        currentSkipCount = 0;
                        break;
                    }
                } else {
                    if (isDelete) {
                        deletesSkipped++;
                    } else {
                        updatesSkipped++;
                    }
                    currentSkipCount++;
                    if (nextnext != null) {
                        nextnext.setSequence(sequence);
                        nextnext.setPublishDate(publishDate);
                    }
                    if (currentSkipCount > keepAliveNulls) {
                        hasNext = true;
                        needsFindNext = false;
                        return;
                    }
                }
                if (count % logBatch == 0) {
                    log.info("{}: sequence: {} count: {}  skipped: updates: {}, deletes: {}", current.getName(), sequence, count, updatesSkipped, deletesSkipped);
                }
            }
            // handle last one
            if (next == null && nextnext != null) {
                next = nextnext;
                next.setSequence(sequence);
                next.setPublishDate(publishDate);
                nextnext = null;
                if (count > logBatch) {
                    log.info("{}: sequence: {} count: {}  skipped: updates: {}, deletes: {}. Ready.", current.getName(), sequence, count, updatesSkipped, deletesSkipped);
                }
            }
            needsFindNext = false;
            hasNext = next != null;
        }
    }

    protected boolean needsOutputAndAdapt(MediaChange input) {
        if (input.isDeleted()) {
            return deleteNeedsOutput(input);
        } else {
            return updateNeedsOutput(input);
        }
    }

    protected boolean deleteNeedsOutput(MediaChange input) {
        if (! (input.getMedia() == null || previous.test(input.getMedia()))) {
            // input does was not in the previous profile, no need to output
            return false;
        }
        return appliesToSince(input);
    }


    protected boolean updateNeedsOutput(MediaChange input) {
        final boolean inCurrent = current.test(input.getMedia());
        final boolean inPrevious = previous.test(input.getMedia());

        if (inCurrent) {
            // Is newer then since or did not apply under previous profile
            return sendAfterSince(input) || !inPrevious;
        } else {
            if (sendBeforeSince(input) && !inPrevious) {
                // Older then since but outside previous as well
                return false;
            }
            // Return a delete since we can't determine whether the previous revision applied to the current
            // profile without extra document retrievals (NPA-134)
            input.setDeleted(true);
            return true;
        }
    }


    protected boolean sendAfterSince(MediaChange input) {
        return appliesToSince(input);
    }

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

    protected boolean sendBeforeSince(MediaChange input) {
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
