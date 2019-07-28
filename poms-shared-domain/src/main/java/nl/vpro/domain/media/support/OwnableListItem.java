package nl.vpro.domain.media.support;

import nl.vpro.domain.Child;

/**
 * A (
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface OwnableListItem<THIS extends OwnableListItem<THIS, P>, P extends OwnableList<P, THIS>> extends Child<P>, Comparable<THIS> {
}
