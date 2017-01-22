package nl.vpro.domain;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Typable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface OwnedText extends Ownable, Typable<TextualType> {

    String get();
    void set(String s);

}
