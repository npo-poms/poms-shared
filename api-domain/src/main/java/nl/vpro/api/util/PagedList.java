/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.util;

import java.util.AbstractList;
import java.util.List;

/**
 * @author Roelof Jan Koekoek
 * @since 1.0
 */
public class PagedList<T> extends AbstractList<T> {
    private final List<T> wrappedList;

    private final long offset;

    private final int max;

    public PagedList(List<T> wrappedList) {
        this(wrappedList, 0l, Integer.MAX_VALUE);
    }

    public PagedList(List<T> wrappedList, long offset, int max) {
        this.wrappedList = wrappedList;
        this.offset = offset > 0 ? offset : 0;
        this.max = Math.max(max, 0);
    }

    @Override
    public T get(int index) {
        return wrappedList.get((int)offset + index);
    }

    @Override
    public int size() {
        int headRoom = wrappedList.size() - (int)offset;
        return headRoom > 0 ? Math.min(headRoom, max) : 0;
    }
}
