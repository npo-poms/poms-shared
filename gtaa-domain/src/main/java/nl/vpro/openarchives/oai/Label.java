/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.openarchives.oai;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import javax.xml.XMLConstants;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class Label {

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI, required = true)
    private String lang;

    @XmlValue()
    private String value;

    public static Label forValue(String value) {
        if(value == null) {
            return null;
        }

        return new Label(value);
    }

    @JsonCreator
    public Label(String value) {
        this.value = value;
        this.lang = "nl";
    }

    @lombok.Builder
    private Label(String value, String lang) {
        this.value = value;
        this.lang = StringUtils.isBlank(lang) ? "nl" : lang;
    }


    Label() {
    }

}
