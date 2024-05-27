package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@XmlEnum
@XmlType(name = "subtitlesWorkflowEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum SubtitlesWorkflow {

    /**
     * Completely excluded from publishing/unpublishing logic. Not a real status, can can only be
     * attained by manually setting it in the database.
     */
    IGNORE(null),

    /**
     * Revoked because the media itself is not published, or is not playable
     */
    REVOKED(null),



    /**
     * Marked deleted, and already revoked from API
     */
    DELETED(null),

     /**
     * Marked for deletion, but not yet processed
     */
    FOR_DELETION(DELETED),

    /**
     * The subtitles are published to the API
     */
    PUBLISHED(null),

    /**
     * New subtitles, not yet processed for publication.
     */
    FOR_PUBLICATION(PUBLISHED),

     /**
     * Changed subtitles, not yet processed for publication
     */
    FOR_REPUBLICATION(PUBLISHED),



    /**
     * Could not be published because of some error
     */
    PUBLISH_ERROR(null),


    /**
     * The subtitles are not available in POMS. But the player probably does have them?
     * This is missed opportunity, but that's how it is.
     * @since 7.11
     */
    MISSING(null)
    ;


    public static final  Set<SubtitlesWorkflow> NEEDS_WORK = Set.of(
        FOR_DELETION,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    );

    public static final  Set<SubtitlesWorkflow> NOT_PUBLISHED = Set.of(
        FOR_DELETION,
        DELETED,
        REVOKED,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    );

    public static final Set<SubtitlesWorkflow> IN_API = Set.of(
        PUBLISHED,
        MISSING
    );

    public static final  Set<SubtitlesWorkflow> DELETEDS = Set.of(
        FOR_DELETION,
        DELETED
    );

    public static final Set<SubtitlesWorkflow> INVISIBLE = Set.of(
        FOR_DELETION,
        DELETED,
        IGNORE
    );


    @Getter
    private final SubtitlesWorkflow dest;

    SubtitlesWorkflow(SubtitlesWorkflow dest) {
        this.dest = dest;
    }
}
