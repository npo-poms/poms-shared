package nl.vpro.domain.gtaa;

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
@XmlType(name = "classification",
    propOrder = {
        "value",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "classification")
public class GTAAClassification extends AbstractSimpleValueGTAAConcept {

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


