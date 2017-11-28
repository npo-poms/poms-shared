package nl.vpro.domain.api.media;

import nl.vpro.domain.api.Change;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.FilteringIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class ChangeIterator implements Iterator<Change> {

    protected static final Logger LOG = LoggerFactory.getLogger(ChangeIterator.class);

    private static final int LOG_BATCH = 50000;

    private final Iterator<Change> wrapped;

    private final ProfileDefinition<MediaObject> current;

    private final ProfileDefinition<MediaObject> previous;

    private final Instant sinceDate;
    private final Long since;

    private Change next;
    private Change nextnext;

    private boolean needsFindNext = true;
    private Boolean hasNext = null; // not known yet

    private Long sequence; // sequence number of most recent DocumentChange (whether filtered or not)
    private Instant publishDate;

    private long count = 0; // total number found until now
    private long updatesSkipped = 0; // how many are skipped in total now
    private long deletedsSkipped = 0; // how many are skipped in total now
    private long currentSkipCount = 0; // how many are skipped in e sequence now
    private final long keepAliveNulls; // after how many sequential skips a 'null' must be returned.

    /**
     * @param iterator      The original couchdb change feed
     * @param since         The since argument, which is sometimes needed during filtering
     * @param current       The current profile as used by filtering
     * @param previous      The profile at the moment 'since'.
     * @param keepAliveNull (optional) If filtering will skip very many changes, the next() call may take several minutes.
     *                      If you set this to some finite positive value (it defaults to MAX_VALUE), every so many skips it wil return 'null'.
     *                      This null can be used to send something to the client to keep the connection alive.
     */

    public ChangeIterator(Iterator<Change> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, long keepAliveNull) {
        this.wrapped = new FilteringIterator<>(iterator, Objects::nonNull);
        this.sinceDate = since;
        this.since = null;
        this.current = nullIsMatchAlways(current);
        this.previous = nullIsMatchAlways(previous);
        this.keepAliveNulls = keepAliveNull;
    }

    public ChangeIterator(Iterator<Change> iterator, Instant since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous) {
        this(iterator, since, current, previous, Long.MAX_VALUE);
    }

    public ChangeIterator(Iterator<Change> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous, long keepAliveNull) {
        this.wrapped = new FilteringIterator<>(iterator, Objects::nonNull);
        this.since = since;
        this.sinceDate = null;
        this.current = nullIsMatchAlways(current);
        this.previous = nullIsMatchAlways(previous);
        this.keepAliveNulls = keepAliveNull;
    }

    public ChangeIterator(Iterator<Change> iterator, Long since, final ProfileDefinition<MediaObject> current, final ProfileDefinition<MediaObject> previous) {
        this(iterator, since, current, previous, Long.MAX_VALUE);
    }

    @Override
    public boolean hasNext() {
        findNext();
        return hasNext;

    }

    @Override
    public Change next() {
        findNext();
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        needsFindNext = true;
        if (currentSkipCount > keepAliveNulls) {
            currentSkipCount = 0;
            return null;
        }
        Change result = next;
        next = null;
        return result;
    }

    public Long getSequence() {
        return sequence;
    }

    public Instant getPublishDate() {
        return publishDate;
    }

    protected void findNext() {
        // we administrate the next change, but also the 'next next' one.
        // See NPA-105
        if (needsFindNext) {
            while (wrapped.hasNext()) {
                Change n = wrapped.next();
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
                        deletedsSkipped++;
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
                if (count % LOG_BATCH == 0) {
                    LOG.info("{}: sequence: {} count: {}  skipped: updates: {}, deletes: {}", current.getName(), sequence, count, updatesSkipped, deletedsSkipped);
                }
            }
            // handle last one
            if (next == null && nextnext != null) {
                next = nextnext;
                next.setSequence(sequence);
                next.setPublishDate(publishDate);
                nextnext = null;
                if (count > LOG_BATCH) {
                    LOG.info("{}: sequence: {} count: {}  skipped: updates: {}, deletes: {}. Ready.", current.getName(), sequence, count, updatesSkipped, deletedsSkipped);
                }
            }
            needsFindNext = false;
            hasNext = next != null;
        }
    }

    protected boolean needsOutputAndAdapt(Change input) {
        if (input.isDeleted()) {
            return deleteNeedsOutput(input);
        } else {
            return updateNeedsOutput(input);
        }
    }

    protected boolean deleteNeedsOutput(Change input) {
        if (! (input.getMedia() == null || previous.test(input.getMedia()))) {
            return false;
        }
        if (! hasSince()) {
            return true;
        }
        if (since == null) {
            return input.getPublishDate() == null || input.getPublishDate().isAfter(sinceDate);
        } else {
            return input.getSequence() > since;
        }
    }


    protected boolean updateNeedsOutput(Change input) {
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


    protected boolean sendAfterSince(Change input) {
        if (! hasSince()) {
            return true;
        }
        if (since != null) {
            return input.getSequence() >= since;
        } else {
            return input.getPublishDate() == null || input.getPublishDate().isAfter(sinceDate);
        }
    }

    protected boolean sendBeforeSince(Change input) {
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
