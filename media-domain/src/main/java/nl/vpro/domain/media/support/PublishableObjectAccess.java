package nl.vpro.domain.media.support;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Some methods that are not part of the public API, but are needed for testing or occosionally for maintenance.

 * E.g. normally you have no business in {@link PublishableObject#setWorkflow(Workflow) <em>setting</em> the workflow),
 * that why the method is protected. If if you need to do it anyway, you can use this class.
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class PublishableObjectAccess {

    private PublishableObjectAccess() {
    }

    /**
     * Just calls the (protected) {@link PublishableObject#setWorkflow(Workflow)}.
     */
    public static void setWorkflow(@NonNull PublishableObject<?> object, Workflow workflow) {
        object.setWorkflow(workflow);
    }

    /**
     * Sets the workflow as it should be when published. e.g. a workflow like {@link Workflow#FOR_DELETION} becomes {@link Workflow#DELETED}. I.e. it sets the workflow to {@link Workflow#getPublishedAs()}
     * @since 5.35
     */
    public static void markPublished(@NonNull PublishableObject<?> object) {
        if (object.getWorkflow() != null) {
            setWorkflow(object, object.getWorkflow().getPublishedAs());
        }
    }
}
