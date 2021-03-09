package nl.vpro.domain.media;

import java.time.Instant;

import org.slf4j.LoggerFactory;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.Trackable;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.Workflow;

/**
 * A trackable object has also {@link Embargo}, and {@link #getWorkflow()}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface TrackableObject extends Trackable, Embargo {

    Workflow getWorkflow();

    /**
     * Wether this object could be  publicly visible in the API.
     *
     * This returns <code>false</code> if the workflow explictely indicates that it is not (like 'DELETED', 'MERGED')
     * and otherwise it depends on {@link #inPublicationWindow(Instant)}
     */
    default boolean isPublishable(Instant now) {
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

            return inPublicationWindow(now) &&
                (getParent() == null || getParent().isPublishable(now));
        }

        LoggerFactory.getLogger(getClass()).error("Unexpected state of {}. Workflow: {}. Supposing this is not publishable for now", this, workflow);
        return false;
    }


    default boolean isRevocable(Instant now) {
        Workflow workflow = getWorkflow();
        if (workflow == Workflow.IGNORE) {
            return false;
        }
        return ! isPublishable(now);
    }

    /**
     * If the sub class supports being merged, this can be overriden.
     *
     */
    default boolean isMerged() {
        return false;
    }

    /**
     * Implementations can optionally become child of a trackable parent where the parents trackability decides on
     * this child's trackability.
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
