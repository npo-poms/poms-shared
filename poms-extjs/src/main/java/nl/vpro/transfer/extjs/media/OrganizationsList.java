/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.user.Organization;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "organizations")
public class OrganizationsList extends TransferList<OrganizationView> {

    public OrganizationsList() {
    }

    public OrganizationsList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static OrganizationsList create(Collection<? extends Organization> allowed, Collection<? extends Organization> preferred) {
        OrganizationsList simpleList = new OrganizationsList();
        simpleList.success = true;

        if (allowed == null) {
            return simpleList;
        }

        for (Organization org : allowed) {
            simpleList.add(OrganizationView.create(org, preferred.contains(org)));
        }
        return simpleList;
    }

    public static OrganizationsList create(Collection<? extends Organization> organizations) {
        OrganizationsList simpleList = new OrganizationsList(true, null);

        if (organizations == null) {
            return simpleList;
        }

        for (Organization org : organizations) {
            simpleList.add(OrganizationView.create(org, false));
        }
        return simpleList;
    }

    public static OrganizationsList create(Organization... organizations) {
        return create(Arrays.asList(organizations));
    }
}
