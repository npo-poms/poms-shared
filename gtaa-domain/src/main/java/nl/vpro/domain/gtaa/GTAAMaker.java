package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.xml.bind.annotation.*;

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
public final class GTAAMaker extends AbstractSimpleValueGTAAConcept {


    @Serial
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
