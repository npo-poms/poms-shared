/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serial;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

@Entity
@Cacheable(true)
@XmlType(name = "thirdPartyType", namespace = Xmlns.MEDIA_NAMESPACE)
public class ThirdParty extends Organization {

    @Serial
    private static final long serialVersionUID = 3250763235873656107L;

    protected ThirdParty() {

    }

    public ThirdParty(String id, String displayName) {
        super(id, displayName);
    }

}
