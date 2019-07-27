package nl.vpro.domain.media.support;

import nl.vpro.domain.Child;
import nl.vpro.domain.Identifiable;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface MediaObjectOwnableList<
    THIS,
    I extends Comparable<I> & Child<THIS> & Identifiable<Long>> extends OwnableList<THIS, I>, Child<MediaObject> {
}
