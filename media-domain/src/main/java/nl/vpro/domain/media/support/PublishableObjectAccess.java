package nl.vpro.domain.media.support;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class PublishableObjectAccess {

    private PublishableObjectAccess() {
    }

    public static void setWorkflow(PublishableObject<?> object, Workflow workflow) {
        object.setWorkflow(workflow);
    }

    /**
     * Sets the workflow as it should be when published. e.g. a workflow like {@link Workflow#FOR_DELETION} becomes {@link Workflow#DELETED}. I.e. it sets the workflow to {@link Workflow#getPublishedAs()}
     * @since 5.35
     */
    public static void markPublished(PublishableObject<?> object) {
        if (object.getWorkflow() != null) {
            setWorkflow(object, object.getWorkflow().getPublishedAs());
        }
    }
}
