/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.support.OwnerType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "type",
        "name"
        })
public class AVTypeView {

    private String type;

    private String name;

    private AVTypeView() {
    }

    private AVTypeView(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AVTypeView create(AVType fullType) {
        return fullType == null ? null : new AVTypeView(fullType.name(), fullType.toString());
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
