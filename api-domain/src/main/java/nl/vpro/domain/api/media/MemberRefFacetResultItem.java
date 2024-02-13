/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.TermFacetResultItem;
import nl.vpro.domain.media.MediaType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefFacetResultItemType")
public class MemberRefFacetResultItem extends TermFacetResultItem {

    private MediaType type;

    public MemberRefFacetResultItem() {

    }

    public MemberRefFacetResultItem(String value, String id, MediaType type, long count) {
        super(value, id, count);
        this.type = type;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

}
