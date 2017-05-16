package nl.vpro.domain.media.support;

import lombok.Getter;

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
     * All subtitles for this object are revoked
     */
    REVOKED_MEDIA(REVOKED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION(REVOKED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION_MEDIA(REVOKED_MEDIA)
    ;

    @Getter
    private final SubtitlesWorkflow target;

    SubtitlesWorkflow(SubtitlesWorkflow target) {
        this.target = target;
    }
}
