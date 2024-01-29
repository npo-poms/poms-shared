package nl.vpro.domain.subtitles;

import java.util.*;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

import static java.util.Collections.unmodifiableSet;

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
    IGNORE,

    /**
     * Revoked because the media itself is not published, or is not playable
     */
    REVOKED,


    /**
     * Marked for deletion, but not yet processed
     */
    FOR_DELETION,

    /**
     * Marked deleted, and already revoked from API
     */
    DELETED,


    /**
     * New subtitles, not yet processed for publication.
     */
    FOR_PUBLICATION,

     /**
     * Changed subtitles, not yet processed for publication
     */
    FOR_REPUBLICATION,

    /**
     * The subtitles are published to the API
     */
    PUBLISHED,

    /**
     * Could not be published because of some error
     */
    PUBLISH_ERROR
    ;


    public static final  Set<SubtitlesWorkflow> NEEDS_WORK =
        unmodifiableSet(new HashSet<>(
            Arrays.asList(
                FOR_DELETION,
                FOR_PUBLICATION,
                FOR_REPUBLICATION
            ))
        );

    public static final  Set<SubtitlesWorkflow> NOT_PUBLISHED = unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, DELETED, REVOKED, FOR_PUBLICATION)));

    public static final  Set<SubtitlesWorkflow> DELETEDS = unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, DELETED)));

    public static final Set<SubtitlesWorkflow> INVISIBLE = DELETEDS;



}
