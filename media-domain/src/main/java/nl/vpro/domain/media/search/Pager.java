/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagerType", namespace = SEARCH_NAMESPACE, propOrder = {
        "offset",
        "max",
        "sort",
        "order"
        })
public class Pager {

    public static enum Direction {
        ASC,
        DESC
    }

    @XmlElement
    private long offset = 0;

    @XmlElement(required = true)
    private Integer max = null;

    @XmlElement
    private String sort;

    @XmlElement
    private Direction order = Direction.ASC;

    public Pager() {
        this(null);
    }

    public Pager(Integer max) {
        this(0, max, "creationDate", Direction.ASC);
    }

    public Pager(long offset, Integer max, String sort, Direction order) {
        if(offset < 0 || (max != null && max < 0) || order == null) {
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

    public String getSort() {
        return sort;
    }

    public Direction getOrder() {
        return order;
    }

}
