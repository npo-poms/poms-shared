/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.*;

@XmlTransient
public abstract class Pager<S extends SortField> {

    public enum Direction {
        ASC,
        DESC
    }

    @XmlElement
    private long offset = 0;

    @XmlElement(required = true)
    private Integer max = null;

    @XmlTransient
    private S sort;

    @XmlElement
    private Direction order = Direction.ASC;

    public Pager(long offset, Integer max, S sort, Direction order) {
        if (order == null) {
            order = Direction.ASC;
        }
        if(offset < 0 || (max != null && max < 0)) {
            throw new IllegalArgumentException(String.format("Must supply valid arguments, got offset: %1$s, max: %d$s %2$s, sort: %3$s, order: %4$s", offset, max, sort, order));
        }

        this.offset = offset;
        this.max = max;
        this.sort = sort;
        this.order = order;

    }

    public Long getOffset() {
        return offset;
    }

    public Integer getMax() {
        return max;
    }

    public S getSort() {
        return sort;
    }

    public void setSort(S sort) {
        this.sort = sort;
    }

    public String getSortField() {
        return getSort().name();
    }

    public Direction getOrder() {
        return order;
    }

}
