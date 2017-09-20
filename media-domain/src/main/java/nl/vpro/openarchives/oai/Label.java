/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
@AllArgsConstructor
@Builder
public class Label {

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI, required = true)
    private String lang = "nl";

    @XmlValue()
    private String value;

    Label() {
    }

    public static Label forValue(String value) {
        if(value == null) {
            return null;
        }

        return new Label(value);
    }

    public Label(String value) {
        this.value = value;
    }
}
