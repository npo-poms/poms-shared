package nl.vpro.domain.constraint.media;

import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import nl.vpro.domain.constraint.AbstractGenreConstraint;
import nl.vpro.domain.media.Genre;
import nl.vpro.domain.media.MediaObject;


/**
 * @author machiel
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genreConstraintType")
@XmlDocumentation("A constraint on the genre id. This may be postfixed with an asterix")
public class GenreConstraint extends AbstractGenreConstraint<MediaObject> {

    public GenreConstraint() {

    }

    public GenreConstraint(String value) {
        super(value);
    }

    @Override
    protected Stream<String> getTermIds(MediaObject p) {
        return p.getGenres().stream().map(Genre::getTermId);
    }
}
