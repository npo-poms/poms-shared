package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes the workflow status of subtitles related to a certain mediaobject.
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum SubtitlesWorkflow {
    /**
     * it is unknown wether any subtitles need publishing or revoking
     */
    UNDEFINED(null, null, MediaSub.PUBLISHED),

    /**
     * All subtitles for this object are published
     */
    PUBLISHED(null, Sub.PUBLISHED, MediaSub.PUBLISHED),

    /**
     * All subtitles for this object are published, but there were new or removed ones, so the mediaobject itself still needs publishing
     */
    PUBLISHED_MEDIA(PUBLISHED, Sub.PUBLISHED, MediaSub.TO_PUBLISH),

    /**
     * Subtitles needs publishing, because something changed, no change in the media object needed
     */
    FOR_PUBLICATION(PUBLISHED, Sub.TO_PUBLISH, MediaSub.PUBLISHED),

    /**
     * Subtitles needs publishing and after that the status needs to become {@link #PUBLISHED_MEDIA}, because also the media objects needs to be published
     */
    FOR_PUBLICATION_MEDIA(PUBLISHED_MEDIA, Sub.TO_PUBLISH, MediaSub.TO_PUBLISH),


    /**
     * All subtitles for this object are revoked
     */
    REVOKED(null, Sub.REVOKED, MediaSub.PUBLISHED),


    /**
     * All subtitles for this object are revoked, but the mediaobject itself needs publishing.
     */
    REVOKED_MEDIA(REVOKED, Sub.REVOKED, MediaSub.PUBLISHED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION(REVOKED, Sub.REVOKED, MediaSub.PUBLISHED),

    /**
     * All subtitles for this object are revoked
     */
    FOR_REVOCATION_MEDIA(REVOKED_MEDIA, Sub.REVOKED, MediaSub.TO_PUBLISH),

    /**
     * Never set by code, but you can update values in the database to make sure that publisher is ignoring this, which can be usefull during testing.
     */
    IGNORE(null, Sub.PUBLISHED, MediaSub.PUBLISHED)
    ;

    /**
     * All workflows indicating that the subtitles themselves are completely published
     */
    public static final Set<SubtitlesWorkflow> PUBLISHED_WORKFLOW = new HashSet<>(Arrays.asList(PUBLISHED, PUBLISHED_MEDIA));

    /**
     * All workflows indicating that the subtitles themselves are completely revoked
     */
    public static final Set<SubtitlesWorkflow> REVOKED_WORKFLOW = new HashSet<>(Arrays.asList(REVOKED, REVOKED_MEDIA));

    /**
     * All workflows indicating that the subtitles are ready, but the associated media still needs republishing.
     */
    public static final Set<SubtitlesWorkflow> MEDIA_WORKFLOW = new HashSet<>(Arrays.asList(REVOKED_MEDIA, PUBLISHED_MEDIA));


    @Getter
    private final SubtitlesWorkflow target;

    /**
     * Indicates the status of the subtitles of the media object.
     * @since 5.11.4
     */
    @Getter
    private final Sub subtitles;

    /**
     * Indicates whether the media object itself needs publishing (if it itself is to be published)
     * @since 5.11.4
     */
    @Getter
    private final MediaSub media;

    SubtitlesWorkflow(SubtitlesWorkflow target, Sub subtitles, MediaSub media) {
        this.target = target;
        this.subtitles = subtitles;
        this.media = media;
    }

    public boolean hasTarget() {
        return target != null;
    }

    enum MediaSub {
        TO_PUBLISH,
        PUBLISHED
    }

    enum Sub {
        TO_REVOKE,
        REVOKED,
        TO_PUBLISH,
        PUBLISHED
    }
}
