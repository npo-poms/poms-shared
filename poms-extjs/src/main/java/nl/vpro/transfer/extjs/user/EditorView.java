/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.user;

import nl.vpro.domain.user.Editor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "editor")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "name"
        })
public class EditorView {
    private String id;

    private String name;

    private EditorView() {
    }

    private EditorView(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EditorView create(Editor user) {
        return new EditorView(user.getPrincipalId(), user.getDisplayName());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
