/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.AVFileFormat;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "format",
        "name"
        })
public class FormatsView {
    private String format;

    private String name;

    private FormatsView() {
    }

    private FormatsView(String format, String name) {
        this.format = format;
        this.name = name;
    }

    public static FormatsView create(AVFileFormat fullFormat) {
        return new FormatsView(fullFormat.name(), fullFormat.toString());
    }

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }
}
