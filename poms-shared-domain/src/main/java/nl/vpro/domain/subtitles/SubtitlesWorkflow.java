package nl.vpro.domain.subtitles;

import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public enum SubtitlesWorkflow {
    IGNORE,

    REVOKED,

    FOR_DELETION,
    DELETED,

    FOR_PUBLICATION,
    FOR_REPUBLICATION,
    PUBLISHED,

    /**
     * Could not be published because of some error
     */
    PUBLISH_ERROR
    ;


    public static final  Set<SubtitlesWorkflow> NEEDS_WORK = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, FOR_PUBLICATION, FOR_REPUBLICATION)));

    public static final Set<SubtitlesWorkflow> NEEDS_MEDIA_PUBLICATION = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FOR_PUBLICATION, FOR_DELETION)));

    public static final  Set<SubtitlesWorkflow> TO_REVOKE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, DELETED, REVOKED)));
}
