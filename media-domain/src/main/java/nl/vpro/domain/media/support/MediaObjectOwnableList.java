package nl.vpro.domain.media.support;

import java.io.Serializable;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.MediaObject;

/**
 * A MediaObjectOwnableList is a {@link OwnableList} that is also a {@link Child} of {@link MediaObject}, and of which the items are {@link MediaObjectOwnableListItem}.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface MediaObjectOwnableList<P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>  extends OwnableList<P, I>, Child<MediaObject>, Serializable {

    P clone();


}

