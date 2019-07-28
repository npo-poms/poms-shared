package nl.vpro.domain.media.support;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface MediaObjectOwnableListItem<THIS extends MediaObjectOwnableListItem<THIS, P>, P extends MediaObjectOwnableList<P, THIS>> extends OwnableListItem<THIS, P>, Cloneable {
    THIS clone();
}
