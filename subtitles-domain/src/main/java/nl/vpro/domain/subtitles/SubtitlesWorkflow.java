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
    PUBLISHED;


    public static final  Set<SubtitlesWorkflow> NEEDS_WORK = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, FOR_PUBLICATION)));
}
