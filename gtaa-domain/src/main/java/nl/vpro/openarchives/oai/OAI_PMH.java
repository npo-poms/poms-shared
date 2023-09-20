/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.openarchives.oai;

import lombok.Data;

import java.time.ZonedDateTime;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

/**
 * See https://docs.google.com/document/d/16L_Gp2awzwa3GHOPNIcK-9LNB0iMZgif-JUfFCMOkkg/edit?pref=2&pli=1
 * @author Roelof Jan Koekoek
 *
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "OAI-PMH")
@Data
public class OAI_PMH {

    @XmlAttribute(namespace = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
    private String schemaLocation;

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime responseDate;

    @XmlElement
    private Request request;

    @XmlElement(name = "error")
    private Error error;

    @XmlElement(name = "ListRecords")
    private ListRecord listRecord;


}

