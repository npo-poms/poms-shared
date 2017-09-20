package nl.vpro.w3.rdf;

import lombok.*;
import nl.vpro.domain.media.gtaa.AbstractGTAAObject;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.Namespaces;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "type",
    "literalForm",
    "tenant"
})
@ToString
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LabelDescription extends AbstractGTAAObject {

    public LabelDescription() {

    }
    @XmlElement(namespace = Namespaces.RDF)
    private ResourceElement type;

    @XmlElement(namespace = Namespaces.SKOS_XL)
    private Label literalForm;

    @XmlElement(namespace = Namespaces.OPEN_SKOS)
    private String tenant;

}
