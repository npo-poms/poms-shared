/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.openarchives.oai;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import javax.xml.XMLConstants;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class Note {

    public Note() {

    }

    public Note(String value) {
        this.value = value;
    }

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI, required = true)
    private String lang = "nl";

    @XmlValue
    private String value;
}
