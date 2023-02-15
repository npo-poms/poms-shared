package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@GTAAScheme(Scheme.classification)
@XmlType(name = "classificationType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "classification")
@Schema(name = "GTAAClassification")
public class GTAAClassification extends AbstractSimpleValueGTAAConcept {

    private static final long serialVersionUID = -7045377163010493587L;

    @lombok.Builder(builderClassName = "Builder")
    public GTAAClassification(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAAClassification() {

    }


    public static GTAAClassification create(Description description) {
        final GTAAClassification answer = new GTAAClassification();
        fill(description, answer);
        return answer;
    }
}


