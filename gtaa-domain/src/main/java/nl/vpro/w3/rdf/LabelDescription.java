package nl.vpro.w3.rdf;

import lombok.*;

import java.net.URI;
import java.util.UUID;

import jakarta.xml.bind.annotation.*;

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
@EqualsAndHashCode(callSuper = true)
public class LabelDescription extends AbstractGTAAObject {

    public LabelDescription() {

    }

    @lombok.Builder
    private LabelDescription(UUID uuid, URI about, ResourceElement type, Label literalForm, String tenant) {
        super(uuid, about);
        this.type = type;
        this.literalForm = literalForm;
        this.tenant = tenant;
    }

    @XmlElement(namespace = Namespaces.RDF)
    private ResourceElement type;

    @XmlElement(namespace = Namespaces.SKOS_XL)
    private Label literalForm;

    @XmlElement(namespace = Namespaces.OPEN_SKOS)
    private String tenant;

}
