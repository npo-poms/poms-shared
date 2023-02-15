package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.maker)
@XmlType(name = "makerType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "maker")
@Schema(name = "GTAAMaker")
public class GTAAMaker extends AbstractSimpleValueGTAAConcept {


    private static final long serialVersionUID = -8943598568198594830L;

    @lombok.Builder(builderClassName = "Builder")
    public GTAAMaker(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAAMaker() {

    }


    public static GTAAMaker create(Description description) {
        final GTAAMaker answer = new GTAAMaker();
        fill(description, answer);
        return answer;
    }


}
