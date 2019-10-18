package nl.vpro.domain.media.support;

<<<<<<< HEAD
import nl.vpro.domain.media.MediaObject;

=======
>>>>>>> b15b9e184... MSE-4619
/**
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum AvailableSubtitlesWorkflow {
<<<<<<< HEAD
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
     *
     */
    REVOKED

=======
    PUBLISHED,
    FOR_PUBLICATION
>>>>>>> b15b9e184... MSE-4619
}
