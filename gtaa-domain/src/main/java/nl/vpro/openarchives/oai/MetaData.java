package nl.vpro.openarchives.oai;

import static nl.vpro.openarchives.oai.Namespaces.RDF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.NoArgsConstructor;

import nl.vpro.w3.rdf.Description;
import nl.vpro.w3.rdf.RDF;

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

    public Description getFirstDescription() {
        if (getRdf() != null && getRdf().getDescriptions() != null && !getRdf().getDescriptions().isEmpty()) {
            return getRdf().getDescriptions().get(0);
        }
        return null;
    }
}
