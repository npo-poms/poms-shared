/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.RoleType;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "roles")
public class RoleList extends TransferList<RoleView> {

    private RoleList() {
    }

    public static RoleList create() {
        RoleList roleList = new RoleList();

        for(RoleType type : RoleType.values()) {
            roleList.add(RoleView.create(type));
        }

        roleList.success = true;
        roleList.results = RoleType.values().length;

        return roleList;
    }
}