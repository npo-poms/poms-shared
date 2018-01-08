package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum SubtitlesWorkflow {
    /**
     * it is unknown wether any subtitles need publishing or revoking
     */
    UNDEFINED(null),
    /**
     * All subtitles for this object are published
     */
    PUBLISHED(null),

    /**
     * All subtitles for this object are published, but there were new or removed ones, so the mediaobject itself still needs publishing
     */
    PUBLISHED_MEDIA(PUBLISHED),

    /**
     * Subtitles needs publishing, but something changed
     */
    FOR_PUBLICATION(PUBLISHED),

    /**
     * Subtitles needs publishing and after that the status needs to become {@link #PUBLISHED_MEDIA}
     */
    FOR_PUBLICATION_MEDIA(PUBLISHED_MEDIA),


    /**
     * All subtitles for this object are revoked
     */
    REVOKED(null),


    /**
     * All subtitles for this object are revoked, but the mediaobject itself needs publishing.
     */
    REVOKED_MEDIA(REVOKED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION(REVOKED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION_MEDIA(REVOKED_MEDIA),

    /**
     * Never set by code, but you can update values in the database to make sure that publisher is ignoring this, which can be usefull during testing.
     */
    IGNORE(null)
    ;

    /**
     * All workflows indicating that the subtitles themselves are not completely published
     */
    public static final Set<SubtitlesWorkflow> PUBLISHED_WORKFLOW = new HashSet<>(Arrays.asList(PUBLISHED, PUBLISHED_MEDIA));
    /**
     * All workflows indicating that the subtitles themselves are not completely revoked
     */
    public static final Set<SubtitlesWorkflow> REVOKED_WORKFLOW = new HashSet<>(Arrays.asList(REVOKED, REVOKED_MEDIA));
    /**
     * All workflows indicating that the subtitles are ready, but the associated media still needs republishing.
     */
    public static final Set<SubtitlesWorkflow> MEDIA_WORKFLOW = new HashSet<>(Arrays.asList(REVOKED_MEDIA, PUBLISHED_MEDIA));


    @Getter
    private final SubtitlesWorkflow target;

    SubtitlesWorkflow(SubtitlesWorkflow target) {
        this.target = target;
    }
}
