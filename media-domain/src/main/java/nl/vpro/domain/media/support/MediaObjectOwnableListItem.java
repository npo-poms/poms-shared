package nl.vpro.domain.media.support;

import nl.vpro.domain.Identifiable;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface MediaObjectOwnableListItem<THIS extends MediaObjectOwnableListItem<THIS, P>, P extends MediaObjectOwnableList<P, THIS>>
    extends OwnableListItem<THIS, P>, Cloneable, Identifiable<Long>  {

    THIS clone();
}
