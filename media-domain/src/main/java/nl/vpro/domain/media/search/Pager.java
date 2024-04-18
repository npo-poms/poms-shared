/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.checkerframework.checker.nullness.qual.Nullable;

@XmlTransient
@ToString
@Data
public abstract class Pager<S extends SortField> {

    public enum Direction {
        ASC,
        DESC
    }

    @XmlElement(required = false)
    @NotNull
    private Long offset = 0L;

    @XmlElement(required = false)
    @Nullable
    private Integer max = null;

    @XmlTransient
    private S sort;

    @XmlElement
    private Direction order = Direction.ASC;

    protected Pager(long offset, Integer max, S sort, Direction order) {
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


    public String getSortField() {
        return getSort().name();
    }

    public boolean hasOffset() {
        return offset != null && offset != 0;
    }

    /**
     * @since 7.11
     */
    public Integer getOffsetAsInteger() {
        return offset == null ? null : offset.intValue();
    }

}
