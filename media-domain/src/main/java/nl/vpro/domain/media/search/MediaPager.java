/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Builder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;
import static nl.vpro.domain.media.search.MediaSortField.creationDate;
import static nl.vpro.domain.media.search.MediaSortField.lastModified;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaPagerType", namespace = SEARCH_NAMESPACE, propOrder = {
        "offset",
        "max",
        "sort",
        "order"
        })
public class MediaPager extends Pager<MediaSortField> {


    @Builder
    public MediaPager(long offset, Integer max, MediaSortField sort, Direction order) {
        super(offset, max, sort, order);
    }

    public MediaPager(Integer max) {
        this(0, max, creationDate, Direction.ASC);
    }


    public MediaPager() {
        this(0, null, creationDate, Direction.ASC);
    }

    @Override
    @XmlElement
    public MediaSortField getSort() {
        return super.getSort();
    }
}
