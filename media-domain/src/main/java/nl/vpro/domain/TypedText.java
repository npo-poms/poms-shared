package nl.vpro.domain;

import java.util.function.Supplier;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Typable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface TypedText extends Typable<TextualType>, Supplier<String> {


    void set(String s);

    @Override
    String get();

}
