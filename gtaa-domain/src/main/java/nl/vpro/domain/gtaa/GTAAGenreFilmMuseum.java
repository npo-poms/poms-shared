package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.genre)
@XmlType(name = "genreFilmMuseumType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "genre")
@Schema(name = "GTAAGenre_FilmMuseum")
public final class GTAAGenreFilmMuseum extends AbstractSimpleValueGTAAConcept {

    @Serial
    private static final long serialVersionUID = 179862471238594606L;

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGenreFilmMuseum(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAAGenreFilmMuseum() {

    }


    public static GTAAGenreFilmMuseum create(Description description) {
        final GTAAGenreFilmMuseum answer = new GTAAGenreFilmMuseum();
        fill(description, answer);
        return answer;
    }

}
