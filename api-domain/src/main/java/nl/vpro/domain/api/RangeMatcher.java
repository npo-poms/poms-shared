/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Date;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rangeMatcherType")
@XmlSeeAlso({Integer.class, Date.class, String.class})
public abstract class RangeMatcher<T extends Comparable<T>> extends AbstractMatcher implements Predicate<T> {

    @XmlAttribute
    private Boolean inclusiveEnd = false;

    @XmlTransient// We don't expose this..
    private  Boolean inclusiveBegin = true;

    @XmlTransient
    protected T begin;

    @XmlTransient
    protected T end;

    public RangeMatcher() {
    }

    protected RangeMatcher(T begin, T end) {
        this.begin = begin;
        this.end = end;
    }

    protected RangeMatcher(T begin, T end, Boolean inclusiveEnd) {
        this.begin = begin;
        this.end = end;
        this.inclusiveEnd = inclusiveEnd;
    }


    protected RangeMatcher(T begin, T end, Boolean inclusiveEnd, Match match) {
        super(match);
        this.begin = begin;
        this.end = end;
        this.inclusiveEnd = inclusiveEnd;
    }

    public abstract T getBegin();

    public abstract void setBegin(T begin);

    public abstract T getEnd();

    public abstract void setEnd(T end);

    public boolean includeEnd() {
        return inclusiveEnd != null ? inclusiveEnd : false;
    }

    public void setInclusiveEnd(Boolean inclusiveEnd) {
        this.inclusiveEnd = inclusiveEnd;
    }

    public boolean includeBegin() {
        return inclusiveBegin != null ? inclusiveBegin : true;
    }

    public void setInclusiveBegin(Boolean inclusiveBegin) {
        this.inclusiveBegin = inclusiveBegin;
    }


    @Override
    public String toString() {
        return "RangeMatcher{begin=" + begin + ", end=" + end + ", inclusiveEnd=" + inclusiveEnd + "}";
    }

    @Override
    public boolean test(@Nullable T t) {
        boolean apply =  (begin == null || begin.compareTo(t) <= 0) &&
                (end == null || (end.compareTo(t) > 0 || (includeEnd() && end.compareTo(t) == 0)));
        return match == Match.NOT ? ! apply : apply;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof RangeMatcher)) {
            return false;
        }

        RangeMatcher that = (RangeMatcher)o;

        if(begin != null ? !begin.equals(that.begin) : that.begin != null) {
            return false;
        }
        if(end != null ? !end.equals(that.end) : that.end != null) {
            return false;
        }
        if(inclusiveEnd != null ? !inclusiveEnd.equals(that.inclusiveEnd) : that.inclusiveEnd != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = inclusiveEnd != null ? inclusiveEnd.hashCode() : 0;
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
