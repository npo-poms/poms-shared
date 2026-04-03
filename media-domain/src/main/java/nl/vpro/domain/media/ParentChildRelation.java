package nl.vpro.domain.media;

/**
 * Represents a parent/child relation between two {@link MidIdentifiable}s. In practice between two {@link MediaObject}s.
 * <p>
 * Poms recognizes three types of such relations:
 *
 * <ol>
 *     <li>'member of' {@link MediaObject#getMemberOf()}</li>
 *     <li>'episode of' {@link Program#getEpisodeOf()}</li>
 *     <li>'segment of' {@link Segment#getSegmentOf()}</li>
 * </ol>
 *
 * @author Michiel Meeuwissen
 * @since 5.13.1
 */
public interface ParentChildRelation {

    default String getParentMid() {
        return getMidRef();
    }

    String getMidRef();

    String getChildMid();

    /**
     * The type of the parent
     */
    MediaType getType();


}
