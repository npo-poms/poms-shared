package nl.vpro.domain;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Typable;

/**
 * A String with a type ({@link TextualType})
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface TypedText extends Typable<TextualType>, Supplier<String>, Comparable<TypedText>, CharSequence {


    void set(String s);

    @Override
    String get();

    default boolean mayContainNewLines() {
        return true;
    }

    @Override
    default int compareTo(@NonNull TypedText title) {
        if (title == null) {
            return -1;
        }

        return (getType() == null ?
            -1 : getType().ordinal()) - (
                title.getType() == null ? -1 : title.getType().ordinal()
        );
    }
    @Override
    default int length() {
        return get().length();
    }

    @Override
    default char charAt(int index) {
        return get().charAt(index);

    }

    @Override
    default CharSequence subSequence(int start, int end) {
        return get().subSequence(start, end);
    }

    default String fullString() {
        return getType() + ":" + get();
    }

}
