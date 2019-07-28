package nl.vpro.domain.media.support;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An 'ownable list' is an iterable of {@link OwnableListItem}, combined with an 'owner' value.
 *
 * It does not actually completely implement {@link List}. {@link #getValues()} does.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface OwnableList<THIS extends OwnableList<THIS, I>, I extends OwnableListItem<I, THIS>> extends Iterable<I>, Comparable<THIS>, Ownable {


    @NonNull
    List<I> getValues();

    default int size() {
        return getValues().size();
    }

    default boolean isEmpty() {
        return getValues().isEmpty();
    }


    default boolean contains(I element) {
        return getValues().contains(element);
    }

    @Override
    @NonNull
    default Iterator<I> iterator() {
        return getValues().iterator();
    }

    default Stream<I> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}
