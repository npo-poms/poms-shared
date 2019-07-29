package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@GTAAScheme(Scheme.topicbandg)
@XmlType(name = "topicbandg",
    propOrder = {
        "value",
        "notes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "topicbandg")
public class GTAATopicBandG extends AbstractSimpleValueGTAAConcept {

    @lombok.Builder(builderClassName = "Builder")
    public GTAATopicBandG(URI id, List<Label> notes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }
    public GTAATopicBandG() {

    }


    public static GTAATopicBandG create(Description description) {
        final GTAATopicBandG answer = new GTAATopicBandG();
        fill(description, answer);
        return answer;
    }
}


