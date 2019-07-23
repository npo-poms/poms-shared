package nl.vpro.domain.gtaa;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.genre)
@XmlType(name = "genre",
    propOrder = {
        "value",
        "notes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "genre")
public class GTAAGenre extends AbstractSimpleValueThesaurusItem {

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGenre(String id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }
    public GTAAGenre() {

    }


    public static GTAAGenre create(Description description) {
        final GTAAGenre answer = new GTAAGenre();
        fill(description, answer);
        return answer;
    }

}
