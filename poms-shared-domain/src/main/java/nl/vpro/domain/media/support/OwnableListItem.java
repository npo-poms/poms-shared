package nl.vpro.domain.media.support;

import nl.vpro.domain.Child;

/**
 * An item in a {@link OwnableList}, that is it is a {@link Child} of that.
 *
 * It also is {@link Comparable}
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 * @param <THIS> The self type reference.
 * @param <P> The parent list type
 */
public interface OwnableListItem<THIS extends OwnableListItem<THIS, P>, P extends OwnableList<P, THIS>>
    extends Child<P>, Comparable<THIS> {
}
