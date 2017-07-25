package nl.vpro.openarchives.oai;

import lombok.Data;

import static nl.vpro.domain.media.gtaa.Namespaces.RDF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class MetaData {
    @XmlElement(name = "RDF", namespace = RDF)
    private nl.vpro.w3.rdf.RDF rdf;
}
