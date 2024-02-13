/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.search.Pager;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "orderTypeEnum")
public enum Order {
    ASC(Pager.Direction.ASC),
    DESC(Pager.Direction.DESC);

    private final Pager.Direction direction;

    Order(Pager.Direction direction) {
        this.direction = direction;
    }

    public Pager.Direction direction() {
        return direction;
    }
}
