/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.w3.rdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@AllArgsConstructor
@Builder
@Data
public class ResourceElement {

    @XmlAttribute
    private String resource;

    @XmlValue
    private String value;

    // Noargs constructor for jaxb.
    public ResourceElement() {

    }


}
