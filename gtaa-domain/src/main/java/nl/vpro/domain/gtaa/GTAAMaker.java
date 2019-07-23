package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.maker)
@XmlType(name = "maker",
    propOrder = {
        "value",
        "notes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "maker")
public class GTAAMaker extends AbstractSimpleValueThesaurusItem {


    @lombok.Builder(builderClassName = "Builder")
    public GTAAMaker(URI id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }
    public GTAAMaker() {

    }


    public static GTAAMaker create(Description description) {
        final GTAAMaker answer = new GTAAMaker();
        fill(description, answer);
        return answer;
    }


}
