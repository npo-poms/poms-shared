/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.*;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 * @param <V> The type of the values defining the range
 * @param <T> The type of the values defining the {@link Matcher}/{@link java.util.function.Predicate}
 * @see SimpleRangeMatcher SimpleRangeMacher, where V and T are the same, which is the common use case.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rangeMatcherType")
public abstract class RangeMatcher<V extends Comparable<V>, T> extends AbstractMatcher<T> {

    @XmlAttribute
    // TODO it is a bit odd that this attribute is not on the _end_ element
    private Boolean inclusiveEnd = null;

    @XmlTransient// We don't expose this..
    private  Boolean inclusiveBegin = true;


    public RangeMatcher() {
    }

    protected RangeMatcher(V begin, V end) {
        this(begin, end, null, null);
    }

    protected RangeMatcher(V begin, V end, Boolean inclusiveEnd) {
        this(begin, end, inclusiveEnd, null);
    }


    protected RangeMatcher(V begin, V end, Boolean inclusiveEnd, Match match) {
        super(match);
        setBegin(begin);
        setEnd(end);
        this.inclusiveEnd = inclusiveEnd;
    }

    public abstract V getBegin();

    public abstract void setBegin(V begin);

    public abstract V getEnd();

    public abstract void setEnd(V end);

    public boolean includeEnd() {
        return inclusiveEnd != null ? inclusiveEnd : defaultIncludeEnd();
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

    protected abstract boolean defaultIncludeEnd();

    @Override
    public String toString() {
        return "RangeMatcher{begin=" + getBegin() + ", end=" + getEnd() + ", inclusiveEnd=" + inclusiveEnd + "}";
    }


    @SuppressWarnings("SimplifiableConditionalExpression")
    protected boolean valueTest(V t) {
        V begin = getBegin();
        V end = getEnd();
        if (t == null) {
            return begin == null && end == null;
        }
        boolean apply = (begin == null || begin.compareTo(t) <= 0) &&
            (end == null || (end.compareTo(t) > 0 || (includeEnd() && end.compareTo(t) == 0)));
        return match == Match.NOT ? !apply : apply;
    }



    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof RangeMatcher)) {
            return false;
        }

        RangeMatcher<V, T> that = (RangeMatcher<V, T>)o;
        V begin = getBegin();
        V end = getEnd();

        if(begin != null ? !begin.equals(that.getBegin()) : that.getBegin() != null) {
            return false;
        }
        if(end != null ? !end.equals(that.getEnd()) : that.getEnd() != null) {
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
        result = 31 * result + (getBegin() != null ? getBegin().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }
}
