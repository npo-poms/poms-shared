package nl.vpro.domain.media.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An 'ownable list' is an iterable of {@link OwnableListItem}, combined with an 'owner' value.
 *
 * It does not actually completely implement {@link List}. {@link #getValues()} does.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 * @param <THIS> The self type, used in {@link Comparable}, and in the item type
 * @param <I> The item type
 */
public interface OwnableList<THIS extends OwnableList<THIS, I>, I extends OwnableListItem<I, THIS>> extends Collection<I>, Comparable<THIS>, Ownable {


    @NonNull
    List<I> getValues();

    // Default implementing Collection

    @Override
    default int size() {
        return getValues().size();
    }

    @Override
    default boolean isEmpty() {
        return getValues().isEmpty();
    }

    @Override
    default boolean contains(Object element) {
        return getValues().contains(element);
    }

    @Override
    @NonNull
    default Iterator<I> iterator() {
        return getValues().iterator();
    }

    @Override
    @NonNull
    default Object[] toArray() {
        return getValues().toArray();

    }

    @Override
    @NonNull
    default <T> T[] toArray(@NonNull T[] a) {
        return getValues().toArray(a);

    }

    @Override
    default boolean add(I value) {
        return getValues().add(value);

    }

    @Override
    default boolean remove(Object o) {
        return getValues().remove(o);

    }

    @Override
    default boolean containsAll(@NonNull Collection<?> c) {
        return getValues().containsAll(c);

    }

    @Override
    default boolean addAll(@NonNull Collection<? extends I> c) {
        return getValues().addAll(c);
    }

    @Override
    default boolean removeAll(@NonNull Collection<?> c) {
        return getValues().removeAll(c);

    }


    @Override
    default boolean retainAll(@NonNull Collection<?> c) {
        return getValues().retainAll(c);

    }

    @Override
    default void clear() {
        getValues().clear();
    }
}
