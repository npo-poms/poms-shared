package nl.vpro.w3.rdf;

import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.gtaa.AbstractGTAAObject;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.Namespaces;

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
@EqualsAndHashCode(callSuper = true)
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
