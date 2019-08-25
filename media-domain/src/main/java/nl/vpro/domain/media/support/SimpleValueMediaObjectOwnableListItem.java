package nl.vpro.domain.media.support;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface SimpleValueMediaObjectOwnableListItem<THIS extends SimpleValueMediaObjectOwnableListItem<THIS, P, V>, P extends MediaObjectOwnableList<P, THIS>, V>
    extends MediaObjectOwnableListItem<THIS, P> {

    V getValue();
}
