/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static nl.vpro.domain.media.gtaa.Namespaces.SKOS_XL_LABEL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import nl.vpro.w3.rdf.LabelDescription;
import nl.vpro.w3.rdf.ResourceElement;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
@Data
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
