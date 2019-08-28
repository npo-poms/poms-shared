package nl.vpro.domain.media;

import java.time.Instant;

import org.slf4j.LoggerFactory;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.Trackable;
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
    default boolean isPublishable() {
        Workflow workflow = getWorkflow();
        if(isMerged() ||
            Workflow.FOR_DELETION == workflow ||
            Workflow.PARENT_REVOKED == workflow || // The parent is revoked so this object itself is not publishable either
            Workflow.DELETED == workflow) {
            // These kind of objects are explicitely not publishable.
            return false;
        }

        if(Workflow.FOR_PUBLICATION.equals(workflow)
            || Workflow.FOR_REPUBLICATION.equals(workflow)
            || Workflow.PUBLISHED.equals(workflow)
            || Workflow.REVOKED.equals(workflow)
            || Workflow.MERGED.equals(workflow)
            || workflow == null /* may happen when property filtering active NPA-493 */) {

            return inPublicationWindow(Instant.now());
        }
        LoggerFactory.getLogger(getClass()).error("Unexpected state of {}. Workflow: {}. Supposing this is not publishable for now", this, workflow);
        return false;

    }


    default boolean isRevocable() {
        return ! isPublishable();
    }

    /**
     * If the sub class supports being merged, this can be overriden.
     *
     */
    default boolean isMerged() {
        return false;
    }

}
