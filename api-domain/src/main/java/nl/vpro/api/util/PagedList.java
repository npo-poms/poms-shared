/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
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
        this(wrappedList, 0L, Integer.MAX_VALUE);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PagedList<?> pagedList = (PagedList<?>) o;

        if (offset != pagedList.offset) return false;
        if (max != pagedList.max) return false;
        return wrappedList != null ? wrappedList.equals(pagedList.wrappedList) : pagedList.wrappedList == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (wrappedList != null ? wrappedList.hashCode() : 0);
        result = 31 * result + (int) (offset ^ (offset >>> 32));
        result = 31 * result + max;
        return result;
    }
}
