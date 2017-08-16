package nl.vpro.domain.media.support;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.MediaObject;

/**
 * Temporary class, to unify all 'childs' of mediaobject (some had 'parent', some had 'mediaobject' property)
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface MediaObjectChild extends Child<MediaObject> {

    @Deprecated
    default MediaObject getMediaObject() {
        return getParent();
    }

    @Deprecated
    default void setMediaObject(MediaObject parent) {
        setParent(parent);
    }
}
