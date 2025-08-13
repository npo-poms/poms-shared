package nl.vpro.domain.media.support;

import java.io.Serializable;
import java.util.List;

import nl.vpro.domain.Child;
import nl.vpro.domain.Identifiable;
import nl.vpro.domain.media.MediaObject;

/**
 * A MediaObjectOwnableList is a {@link OwnableList} that is also a {@link Child} of {@link MediaObject}, and of which the items are {@link MediaObjectOwnableListItem}.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface MediaObjectOwnableList<
    THIS extends MediaObjectOwnableList<THIS, I>,
    I extends MediaObjectOwnableListItem<I, THIS>>
    extends
    OwnableList<THIS, I>,
    Child<MediaObject>,
    Serializable,
    Identifiable<Long>, Cloneable {

    /**
     * Returns the values in this list, optionally filtered for XML/json. The case was that inherited targetgrups can be filtered using the agerating of the mediaobject.
     * @since 8.10
     */
    List<I> getFilteredValues();


    THIS clone();

}

