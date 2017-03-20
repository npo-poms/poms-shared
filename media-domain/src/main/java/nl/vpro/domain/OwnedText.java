package nl.vpro.domain;

import nl.vpro.domain.media.support.Ownable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface OwnedText<T> extends Ownable, TypedText, Comparable<T> {

}
