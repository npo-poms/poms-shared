/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.List;

@XmlType(
    propOrder =
        {
            "count",
            "result"
        }
)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractScoredSearchResult<T> implements ScoredSearchResult<T> {

    @Getter
    @Setter
    protected Integer count;

    protected List<ScoredResult<T>> result;

    public AbstractScoredSearchResult() {
    }

    protected AbstractScoredSearchResult(final Integer count, final List<ScoredResult<T>> result) {
        this.count = count;
        this.result = Collections.unmodifiableList(result);
    }

    @Override
    public final List<ScoredResult<T>> getScoredResult() {
        return result;
    }

    public final void setScoredResult(List<ScoredResult<T>> l) {
        this.result = Collections.unmodifiableList(l);
        this.count = l.size();
    }
    

    @Override
    public String toString() {
        return "" + result;
    }
 
}
