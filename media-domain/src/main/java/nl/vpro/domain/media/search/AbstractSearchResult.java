/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.*;

@XmlType(
    name ="searchResult",
    propOrder =
        {
            "count",
            "result"
        }
)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractSearchResult<T> implements SearchResult<T> {

    /**
     * The total number of results (when no pager would have been applied)
     */
    @Getter
    @Setter
    protected Long count;

    protected List<T> result;

    public AbstractSearchResult() {
    }

    protected AbstractSearchResult(final Long  count, final List<T> result) {
        this.count = count;
        this.result = Collections.unmodifiableList(result);
    }

    @Override
    public final List<T> getResult() {
        return result;
    }

    public final void setResult(List<T> l) {
        this.result = Collections.unmodifiableList(l);
        this.count = (long) l.size();
    }


    @Override
    public String toString() {
        return "" + result;
    }

    /**
     * @since 7.2
     */
    public Stream<T> stream() {
        return result == null ? Stream.empty() :  result.stream();
    }
}
