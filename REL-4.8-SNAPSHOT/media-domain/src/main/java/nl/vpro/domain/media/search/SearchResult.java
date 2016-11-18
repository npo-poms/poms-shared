/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(
    propOrder =
        {
            "count",
            "result"
        }
)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class SearchResult<T> implements Iterable<T> {

    protected Integer count;

    protected List<T> result;

    public SearchResult() {
    }

    protected SearchResult(final Integer count, final List<T> result) {
        this.count = count;
        this.result = Collections.unmodifiableList(result);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer c) {
        this.count = c;
    }

    public final List<T> getResult() {
        return result;
    }

    public final void setResult(List<T> l) {
        this.result = Collections.unmodifiableList(l);
        this.count = l.size();
    }


    //@Override
    public int size() {
        return result.size();
    }

    @Override
    public String toString() {
        return "" + result;
    }

    @Override
    public Iterator<T> iterator() {
        return result.iterator();
    }
}
