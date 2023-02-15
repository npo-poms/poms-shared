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
 * @since 5.5
 */
@GTAAScheme(Scheme.topic)
@XmlType(name = "topicType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "topic")
@Schema(name = "GTAATopic")
public class GTAATopic extends AbstractSimpleValueGTAAConcept {

    private static final long serialVersionUID = -6679803582581099101L;

    @lombok.Builder(builderClassName = "Builder")
    public GTAATopic(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAATopic() {

    }


    public static GTAATopic create(Description description) {
        final GTAATopic answer = new GTAATopic();
        fill(description, answer);
        return answer;
    }
}


