package nl.vpro.domain;

import java.util.function.Supplier;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Typable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface OwnedText<T> extends Ownable, Typable<TextualType>, Supplier<String>, Comparable<T> {

    void set(String s);

    @Override
    String get();

}
