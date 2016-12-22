/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeFacetItemType")
public class DateRangeFacetItem implements RangeFacetItem<Date> {

    private String name;

    private Date begin;

    private Date end;

    public DateRangeFacetItem() {
    }

    public DateRangeFacetItem(String name, Date begin, Date end) {
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "DateRangeFacetItem{name='" + name + "', begin=" + begin + ", end=" + end + '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof DateRangeFacetItem)) {
            return false;
        }

        DateRangeFacetItem that = (DateRangeFacetItem)o;

        if(begin != null ? !begin.equals(that.begin) : that.begin != null) {
            return false;
        }
        if(end != null ? !end.equals(that.end) : that.end != null) {
            return false;
        }
        if(name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public boolean matches(Date begin, Date end) {
        return (this.begin == null || this.begin.equals(begin))
            &&
            (this.end == null || this.end.equals(end));
    }
}
