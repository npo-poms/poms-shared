package nl.vpro.domain.media;

import com.google.common.annotations.Beta;

import nl.vpro.domain.*;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.Title;
import nl.vpro.nicam.NicamRated;

/**
 * This interface extends most of  the interfaces that {@link MediaObject} itself used to implement.
 * <p>
 * This shouldn't make a difference for now, and it is experimental to see whether is convenient to program against
 * this interface rather than against {@link MediaObject} itself.
 * <p>
 * Some considerations:
 * <p>
 *  This interface makes the object 'mutable'. It may be useful to have a two-step extension mechanisms 'MutableMedia extends Media'.
 *  E.g. a consumer of the frontend api is not interested in mutability.
 * <p>
 *
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@Beta
public interface Media<T extends Media<T>> extends
    NicamRated,
    LocalizedObject<Title, Description, Website, SocialRef, T>,
    TrackableMedia,
    MediaIdentifiable ,
    MutableEmbargo<T>,
    Identifiable<Long>,
    TrackableObject {
}
