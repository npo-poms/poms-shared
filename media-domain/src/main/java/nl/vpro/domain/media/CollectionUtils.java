package nl.vpro.domain.media;

import java.util.*;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Some static methods used in this package that are related to dealing with collections
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Given a collection of values, and a list of object to update, updates the list, optionally creating one first.
     *
     */
    static <T> List<T> updateList(@Nullable List<T> toUpdate, @Nullable Collection<? extends T> values) {
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
        }
        if (toUpdate == null) {
            toUpdate = new ArrayList<>();
        } else {
            if (toUpdate.equals(values)) {
                // the object is already exactly correct, do nothing
                return toUpdate;
            }
            toUpdate.clear();
        }
        if (values != null) {
            toUpdate.addAll(values);
        }
        return toUpdate;
    }

    @SuppressWarnings("unchecked")
    static <T extends Comparable<?>> Set<T> updateSortedSet(Set<T> toUpdate, Collection<T> values) {
        if (toUpdate == values && toUpdate instanceof SortedSet<?> sortedSet) {
            return (SortedSet<T>) sortedSet;
        }
        if (toUpdate == null) {
            toUpdate = new TreeSet<>();
            if (values != null) {
                toUpdate.addAll(values);
            }

        } else {
            if (values != null) {
                toUpdate.retainAll(values);
                for (T v : values) {
                    for (T toUpdateValue : toUpdate) {
                        if (toUpdateValue instanceof Updatable && toUpdateValue.equals(v)) {
                            ((Updatable<T>) toUpdateValue).update(v);
                        }
                    }
                }
                toUpdate.addAll(values);
            }
        }
        return toUpdate;
    }

    @Nullable
    protected static <E> E getFromList(@Nullable List<@NonNull E> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static <E> int indexOf(List<E> list, E o, int offset) {
        ListIterator<E> it = list.listIterator();
        while(offset-- > 0) {
            it.next();
        }
        if (o == null) {
            while (it.hasNext())
                if (it.next()==null)
                    return it.previousIndex();
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return it.previousIndex();
        }
        return -1;
    }

    public static <P extends Comparable<P>> SortedSet<P> createIfNull(SortedSet<P> set) {
        if(set == null) {
            set = new TreeSet<>();
        }
        return set;
    }

    @Nullable
    public static <P extends Comparable<P>> SortedSet<P> createIfNullUnlessNull(@Nullable  SortedSet<P> set, @Nullable  Object check) {
        if (check == null) {
            return null;
        } else {
            if (set == null) {
                set = new TreeSet<>();
            }
            return set;
        }
    }

    /**
     * {@code null}-safe {@link Collection#contains(Object)}. {@code null} is never in the list.
     * <p>
     * Mainly because link java 9's Immutable lists resulting from things as {@link List#of()} throw exception if called with {@code null}
     * (Things like {@link Collections#unmodifiableList(List)} didn't behave like that).
     * <p>
     * Note that the sets in e.g. {@link nl.vpro.domain.media.support.Workflow} are not wrapped with {@link #nullSafeSet(Set)} and don't need this anymore.
     *
     * @since 7.2
     * @see #nullSafeSet(Set)
     */
    public static <P> boolean inCollection(@NonNull Collection<@NonNull P> col, @Nullable P element) {
        return element != null && col.contains(element);
    }


    /**
     * Wraps the given set in a new set, with the same elements.
     * <p>
     * The only difference will be that its {@link Set#contains(Object)} will simply return {@code false} if the argument is {@code null}. Unless the given set argument itself contains {@code null}, then also this set will contain it.
     * @since 7.10
     */
    public static <P> Set<@NonNull P> nullSafeSet(@NonNull final Set<@NonNull P> set) {
        boolean containsNull = false;
        try {
            containsNull = set.contains(null);
        } catch (NullPointerException npe) {
            // ignore
        }
        return nullSafeSet(set, containsNull);
    }

    public static <P> Set<@NonNull P> nullSafeSet(@NonNull final Set<@NonNull P> set, boolean containsNull) {
        return new AbstractSet<P>() {
            @Override
            public @NonNull Iterator<P> iterator() {
                return set.iterator();
            }

            @Override
            public int size() {
                return set.size();
            }

            @Override
            public boolean add(@NonNull P o) {
                return set.add(o);
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return (o == null && containsNull) || (o != null && set.contains(o));
            }
        };
    }



    /**
     * Like {@link Collection#removeIf(Predicate)} but returns the number of removed items.
     * TODO it seems odd that we would be the first to want this? Guava?
     * @since 7.10
     */
    public static <E> int removeIf(Collection<E> collection, Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        int result = 0;
        final Iterator<E> each = collection.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                result++;
            }
        }
        return result;
    }

}
