package nl.vpro.util;

import java.util.SortedSet;

/**
 * @author Michiel Meeuwissen
 * @since 2.3.1
 */
public abstract class SortedSetSameElementWrapper<T> extends SortedSetElementWrapper<T, T> {
    public SortedSetSameElementWrapper(SortedSet<T> wrapped) {
        super(wrapped);
    }

    @Override
    protected T find(T element) {
        return element;
    }

    @Override
    public boolean add(T element) {
        return wrapped.add(element);
    }
}
