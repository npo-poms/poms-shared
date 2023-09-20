/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.Namespaces;
import nl.vpro.w3.rdf.LabelDescription;
import nl.vpro.w3.rdf.ResourceElement;

import static nl.vpro.openarchives.oai.Namespaces.SKOS_XL_LABEL;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class XLLabel extends AbstractGTAAObject {

    @XmlElement(name ="Description", namespace = Namespaces.RDF)
    private LabelDescription description;

    private XLLabel() {
    }
    public XLLabel(String label, String tenant) {
        description = LabelDescription.builder()
            .type(new ResourceElement(SKOS_XL_LABEL, null))
            .literalForm(new Label(label))
            .tenant(tenant)
            .build();
    }

}
