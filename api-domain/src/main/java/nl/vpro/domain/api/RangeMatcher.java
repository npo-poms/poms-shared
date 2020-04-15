/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Date;

import javax.xml.bind.annotation.*;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rangeMatcherType")
@XmlSeeAlso({Integer.class, Date.class, String.class})
public abstract class RangeMatcher<T extends Comparable<T>> extends AbstractMatcher {

    @XmlAttribute
    // TODO it is a bit odd that this attribute is not on the _end_ element
    private Boolean inclusiveEnd = null;

    @XmlTransient// We don't expose this..
    private  Boolean inclusiveBegin = true;


    public RangeMatcher() {
    }

    protected RangeMatcher(T begin, T end) {
        this(begin, end, null, null);
    }

    protected RangeMatcher(T begin, T end, Boolean inclusiveEnd) {
        this(begin, end, inclusiveEnd, null);
    }


    protected RangeMatcher(T begin, T end, Boolean inclusiveEnd, Match match) {
        super(match);
        setBegin(begin);
        setEnd(end);
        this.inclusiveEnd = inclusiveEnd;
    }

    public abstract T getBegin();

    public abstract void setBegin(T begin);

    public abstract T getEnd();

    public abstract void setEnd(T end);

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

    protected boolean testComparable(T t) {
        boolean apply = (getBegin()== null || getBegin().compareTo(t) <= 0) &&
            (getEnd() == null || (getEnd().compareTo(t) > 0 || (includeEnd() && getEnd().compareTo(t) == 0)));
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

        RangeMatcher<T> that = (RangeMatcher<T>)o;
        T begin = getBegin();
        T end = getEnd();

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
