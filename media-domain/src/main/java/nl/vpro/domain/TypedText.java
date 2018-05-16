package nl.vpro.domain;

import java.util.function.Supplier;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Typable;

/**
 * A String with a type ({@link TextualType})
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface TypedText extends Typable<TextualType>, Supplier<String>, Comparable<TypedText> {


    void set(String s);

    @Override
    String get();

    @Override
    default int compareTo(TypedText title) {
        if (title == null) {
            return -1;
        }

        return (getType() == null ?
            -1 : getType().ordinal()) - (
                title.getType() == null ? -1 : title.getType().ordinal()
        );
    }

}
