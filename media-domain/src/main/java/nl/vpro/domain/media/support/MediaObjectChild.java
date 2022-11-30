package nl.vpro.domain.media.support;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.MediaObject;

/**
 * Temporary interface, to unify all 'childs' of mediaobject (some had 'parent', some had 'mediaobject' property)
 * It just add deprecated default methods to make 'get/setMediaobject' available.
 * @author Michiel Meeuwissen
 * @since 5.5
 *
 */
@Deprecated
public interface MediaObjectChild extends Child<MediaObject> {


}
