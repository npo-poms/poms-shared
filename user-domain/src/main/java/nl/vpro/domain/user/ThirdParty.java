/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable(true)
public class ThirdParty extends Organization {

    protected ThirdParty() {

    }

    public ThirdParty(String id, String displayName) {
        super(id, displayName);
    }

}
