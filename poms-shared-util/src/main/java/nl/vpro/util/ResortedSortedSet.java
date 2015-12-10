package nl.vpro.util;

import java.io.Serializable;
import java.util.*;

/**
 * A modifiable SortedSet, that wrapped another collection (changes are reflected), with an explicit sort order.
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class ResortedSortedSet<T> extends AbstractSet<T> implements SortedSet<T>, Serializable {

    private static final long serialVersionUID = 0l;

    final Collection<T> wrapped;
    final SortedSet<T> set;

    public ResortedSortedSet(Collection<T> wrapped, Comparator<T> comparator) {
        set = new TreeSet<>(comparator);
        set.addAll(wrapped);
        this.wrapped = wrapped;
    }

    public ResortedSortedSet(SortedSet<T> set, Collection<T> wrapped) {
        this.set = set;
        this.wrapped = wrapped;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> i = set.iterator();
        return new Iterator<T>() {

            T lastElement = null;
            @Override
            public boolean hasNext() {
                return i.hasNext();

            }

            @Override
            public T next() {
                lastElement = i.next();
                return lastElement;
            }

            @Override
            public void remove() {
                i.remove();
                if (lastElement != null) {
                    wrapped.remove(lastElement);
                }

            }
        };

    }

    @Override
    public int size() {
        return set.size();

    }
    @Override
    public boolean add(T element) {
        set.add(element);
        return wrapped.add(element);
    }

    @Override
    public Comparator<? super T> comparator() {
        return set.comparator();

    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new ResortedSortedSet<>(set.subSet(fromElement, toElement), wrapped);

    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new ResortedSortedSet<>(set.headSet(toElement), wrapped);

    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new ResortedSortedSet<>(set.tailSet(fromElement), wrapped);

    }

    @Override
    public T first() {
        return set.first();

    }

    @Override
    public T last() {
        return set.last();
    }
}
