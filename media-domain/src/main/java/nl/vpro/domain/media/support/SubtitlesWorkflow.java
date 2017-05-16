package nl.vpro.domain.media.support;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum SubtitlesWorkflow {
    /**
     * it is unknown wether any subtitles need publishing or revoking
     */
    UNDEFINED,
    /**
     * All subtitles for this object are published
     */
    PUBLISHED,
    /**
     * Subtitles needs publishing, but something changed
     */
    FOR_PUBLICATION,
    /**
     * Subtitles needs publishing and after that the status needs to become {@link #PUBLISHED_NEW}
     */
    FOR_PUBLICATION_NEW,

    /**
     * All subtitles for this object are published, but there were new or removed ones, so the mediaobject itself still needs publishing
     */
    PUBLISHED_NEW,

    /**
     * All subtitles for this object are revoked
     */
    REVOKED
}
