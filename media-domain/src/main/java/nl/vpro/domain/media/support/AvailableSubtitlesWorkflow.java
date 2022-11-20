package nl.vpro.domain.media.support;

import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum AvailableSubtitlesWorkflow {

    /**
     * The mediaobject is properly published with respect to its subtitles.
     */
    PUBLISHED,

    /**
     * The mediaobject needs republication because it gained or loosed publishable subtitles
     */
    FOR_PUBLICATION,

    /**
     * During publishing of the mediaobject {@link nl.vpro.domain.media.MediaObjects#subtitlesMayBePublished(MediaObject)}, returned false.
     */
    REVOKED,

    /**
     * There are no subtitles (yet).
     * @since 7.1
     */
    NONE

}
