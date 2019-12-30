package nl.vpro.domain.media;

import java.util.*;

/**
 * Some static methods used in this package that are related to dealing with collections
 * @author Michiel Meeuwissen
 * @since 5.12
 */
class CollectionUtils {

    /**
     * Given a collection of values, and a list of object to update, updates the list, optionally creating one first.
     *
     */
    static <T> List<T> updateList(List<T> toUpdate, Collection<? extends T> values) {
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
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
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
                            ((Updatable) toUpdateValue).update(v);
                        }
                    }
                }
                toUpdate.addAll(values);
            }
        }
        return toUpdate;
    }

    protected static <E> E getFromList(List<E> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static <P extends Comparable<P>> SortedSet<P> createIfNull(SortedSet<P> set) {
        if(set == null) {
            set = new TreeSet<P>();
        }
        return set;
    }

    public static <P extends Comparable<P>> SortedSet<P> createIfNullUnlessNull(SortedSet<P> set, Object check) {
        if (check == null) {
            return null;
        } else {
            if (set == null) {
                set = new TreeSet<P>();
            }
            return set;
        }
    }

}
