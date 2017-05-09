/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "portalType", namespace = Xmlns.MEDIA_NAMESPACE)
@Cacheable
public class Portal extends Organization {

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
