/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.user.User;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "name"
        })
public class UserView {
    private String id;

    private String name;

    private UserView() {
    }

    private UserView(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static UserView create(User user) {
        return new UserView(user.getPrincipalId(), user.getDisplayName());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
