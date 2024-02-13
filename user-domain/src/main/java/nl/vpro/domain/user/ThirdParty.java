/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serial;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * Third parties are organisation that are not {@link Broadcaster}s, or {@link Portal}s
 *
 * @TODO There are _no_ third parties defined in production
 */
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
