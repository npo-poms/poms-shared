package nl.vpro.domain.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class RangeFacetResultItem<T extends Comparable<T>> extends FacetResultItem {

    @XmlAttribute
    private String value;

    protected T begin;

    protected T end;

    public RangeFacetResultItem(String value, T begin, T end, long count) {
        super(count);
        this.value = value;
        this.begin = begin;
        this.end = end;
    }

    protected RangeFacetResultItem() {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String name) {
        this.value = name;
    }

    public abstract T getBegin();

    public abstract T getEnd();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RangeFacetResultItem<?> that = (RangeFacetResultItem<?>) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (begin != null ? !begin.equals(that.begin) : that.begin != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value + " " + getBegin() + " - " + getEnd() + " : " + getCount();
    }
}
