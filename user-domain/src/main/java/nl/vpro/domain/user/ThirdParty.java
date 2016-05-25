/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

@Entity
@Cacheable(true)
@XmlType(name = "thirdPartyType", namespace = Xmlns.MEDIA_NAMESPACE)
public class ThirdParty extends Organization {

    protected ThirdParty() {

    }

    public ThirdParty(String id, String displayName) {
        super(id, displayName);
    }

}
