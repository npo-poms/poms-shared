/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.w3.rdf;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vpro.openarchives.oai.Namespaces;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "RDF")
@Data
@NoArgsConstructor
public class RDF {

    public RDF(Description desc) {
        this.descriptions = new ArrayList<>();
        this.descriptions.add(desc);
    }

    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Long start;


    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Long numFound;


    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Float maxScore;

    @XmlElement(name = "Description")
    private List<Description> descriptions = new ArrayList<>();

}
