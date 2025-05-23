/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(
    propOrder =
        {
            "count",
            "query",
            "result"
        }
)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractScoredSearchResult<T> implements ScoredSearchResult<T> {

    /**
     * The total number of results (when no pager would have been applied)
     */
    @Getter
    @Setter
    protected Long count;


    /**
     * A string describing the query issues to obtain this result
     * @since 8.4
     */
    @Getter
    @Setter
    protected String query;

    protected List<ScoredResult<T>> result;

    public AbstractScoredSearchResult() {
    }

    protected AbstractScoredSearchResult(final Long count, final List<ScoredResult<T>> result) {
        this.count = count;
        this.result = Collections.unmodifiableList(result);
    }

    @Override
    public final List<ScoredResult<T>> getScoredResult() {
        return result;
    }

    public final void setScoredResult(List<ScoredResult<T>> l) {
        this.result = Collections.unmodifiableList(l);
        this.count = (long) l.size();
    }


    @Override
    public String toString() {
        return "" + result;
    }

}
