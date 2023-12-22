/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serial;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * A portal is a website, which may or may not be associated to a one specific {@link Broadcaster}. Some broadcasters maintain multiple portals.
 * <p>
 * Content may be associated to a portal. Users may also be associated to a portal. A user may edit content if they shared either a broadcaster or a portal (or a {@link ThirdParty})
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "portalType", namespace = Xmlns.MEDIA_NAMESPACE)
@Cacheable
public class Portal extends Organization {

    @Serial
    private static final long serialVersionUID = 2524006843893831462L;

    public Portal() {
    }

    public Portal(String id) {
        this(id, null);
    }

    @lombok.Builder(builderClassName = "Builder")
    public Portal(String id, String displayName) {
        super(id, displayName);
    }

}
