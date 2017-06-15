/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.RoleType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "role",
        "name"
        })
public class RoleView {

    private String role;

    private String name;

    private RoleView() {
    }

    private RoleView(String role, String name) {
        this.role = role;
        this.name = name;
    }

    public static RoleView create(RoleType fullRole) {
        return new RoleView(fullRole.name(), fullRole.toString());
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}