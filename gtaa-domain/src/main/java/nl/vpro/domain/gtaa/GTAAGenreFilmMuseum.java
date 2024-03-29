package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.genrefilmmuseum)
@XmlType(name = "genreFilmMuseumType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "genreFilmMuseum")
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
