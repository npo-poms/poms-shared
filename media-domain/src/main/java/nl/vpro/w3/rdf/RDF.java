/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.w3.rdf;

import lombok.Data;
import nl.vpro.domain.media.gtaa.Namespaces;

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
public class RDF {

    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Long start;


    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Long numFound;


    @XmlAttribute(namespace = Namespaces.OPEN_SKOS)
    private Float maxScore;

    @XmlElement(name = "Description")
    private List<Description> descriptions = new ArrayList<>();

}
