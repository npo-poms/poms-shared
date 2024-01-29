package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.xml.bind.annotation.*;

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
public final class GTAAClassification extends AbstractSimpleValueGTAAConcept {

    @Serial
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


