package nl.vpro.domain.media;

import java.time.Instant;

import org.slf4j.LoggerFactory;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.Trackable;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.Workflow;

import static nl.vpro.domain.Changeables.instant;

/**
 * A trackable object has also {@link Embargo}, and {@link #getWorkflow()}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface TrackableObject extends Trackable, Embargo {

    Workflow getWorkflow();

    /**
     * Whether this object could be  publicly visible in the API.
     * <p>
     * This returns <code>false</code> if the workflow explicitly indicates that it is not (like 'DELETED', 'MERGED')
     * and otherwise it depends on {@link #inPublicationWindow(Instant)}
     */
    default boolean isPublishable(Instant now) {
        if (! isConsiderableForPublication()) {
            return false;
        }
        return inPublicationWindow(now) &&
            (getParent() == null || getParent().isPublishable(now));
    }

    @Override
    default boolean isConsiderableForPublication() {
        Workflow workflow = getWorkflow();
        if(isMerged() ||
            Workflow.FOR_DELETION == workflow ||
            Workflow.DELETED == workflow ||
            Workflow.IGNORE == workflow
        ) {
            // These kind of objects are explicitly not publishable.
            return false;
        }
        if(Workflow.FOR_PUBLICATION.equals(workflow)
            || Workflow.FOR_REPUBLICATION.equals(workflow)
            || Workflow.PUBLISHED.equals(workflow)
            || Workflow.REVOKED.equals(workflow)
            || Workflow.PARENT_REVOKED.equals(workflow)
            || Workflow.MERGED.equals(workflow)
            || workflow == null /* may happen when property filtering active NPA-493 */) {
            return true;
        }
        LoggerFactory.getLogger(getClass()).error("Unexpected state of {}. Workflow: {}. Supposing this is not publishable for now", this, workflow);


        return false;
    }

    @Override
    default boolean isPublishable() {
        return isPublishable(instant());
    }

    default boolean isRevocable(Instant now) {
        Workflow workflow = getWorkflow();
        if (workflow == Workflow.IGNORE) {
            return false;
        }
        return ! isPublishable(now);
    }

    /**
     * If the subclass supports being merged, this can be overriden.
     *
     */
    default boolean isMerged() {
        return false;
    }

    /**
     * Implementations can optionally become child of a trackable parent where the parents track-ability decides on
     * this child's track-ability.
     *
     * @return a trackable parent, null when a parent is not set or no parent-child relation exists
     * @see Image#getParent()
     * @see Location#getParent()
     * @see Segment#getParent()
     */
    default TrackableObject getParent() {
        return null;
    }
}
