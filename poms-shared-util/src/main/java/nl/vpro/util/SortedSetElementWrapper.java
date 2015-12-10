package nl.vpro.util;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * @author Michiel Meeuwissen
 * @since 2.3.1
 */
public abstract class SortedSetElementWrapper<T, S> extends AbstractSet<S> implements SortedSet<S> {

    protected final SortedSet<T> wrapped;

    public SortedSetElementWrapper(SortedSet<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {
            final Iterator<T> iterator = wrapped.iterator();
            @Override
            public boolean hasNext() {
                return iterator.hasNext();

            }

            @Override
            public S next() {
                return adapt(iterator.next());

            }

            @Override
            public void remove() {
                iterator.remove();

            }
        };
    }


    @Override
    public int size() {
        return wrapped.size();

    }

    @Override
    public Comparator<? super S> comparator() {
        return new Comparator<S>() {
            @Override
            public int compare(S o1, S o2) {
                return wrapped.comparator().compare(find(o1), find(o2));

            }
        };

    }

    @Override
    public SortedSet<S> subSet(S fromElement, S toElement) {
        return sub(wrapped.subSet(find(fromElement), find(toElement)));
    }

    @Override
    public SortedSet<S> headSet(S toElement) {
        return sub(wrapped.headSet(find(toElement)));

    }

    @Override
    public SortedSet<S> tailSet(S fromElement) {
        return sub(wrapped.tailSet(find(fromElement)));
    }

    @Override
    public S first() {
        return adapt(wrapped.first());

    }

    @Override
    public S last() {
        return adapt(wrapped.last());
    }

    protected SortedSet<S> sub(SortedSet<T> wrapped) {
        return new SortedSetElementWrapper<T, S>(wrapped) {
            @Override
            protected S adapt(T element) {
                return SortedSetElementWrapper.this.adapt(element);

            }
        };
    }

    protected T find(S element) {
        for (T t : wrapped) {
            if (adapt(t).equals(element)) {
                return t;
            }
        }
        return null;
    }


    protected abstract S adapt(T element);
}
