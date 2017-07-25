package nl.vpro.openarchives.oai;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vpro.w3.rdf.Description;
import nl.vpro.w3.rdf.RDF;

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
@NoArgsConstructor
public class MetaData {
    @XmlElement(name = "RDF", namespace = RDF)
    private RDF rdf;
    
    public MetaData(Description desc) {
        this.rdf = new RDF(desc);
    }
}
