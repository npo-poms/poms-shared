package nl.vpro.domain.media.support;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class PublishableObjectAccess {


    public static void setWorkflow(PublishableObject<?> object, Workflow workflow) {
        object.setWorkflow(workflow);
    }
}
