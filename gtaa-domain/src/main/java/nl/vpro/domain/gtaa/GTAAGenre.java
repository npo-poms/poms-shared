package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.genre)
@XmlType(name = "genreType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "genre")
@Schema(name = "GTAAGenre")
public class GTAAGenre extends AbstractSimpleValueGTAAConcept {

    private static final long serialVersionUID = 3730679636738125602L;

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGenre(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAAGenre() {

    }


    public static GTAAGenre create(Description description) {
        final GTAAGenre answer = new GTAAGenre();
        fill(description, answer);
        return answer;
    }

}
