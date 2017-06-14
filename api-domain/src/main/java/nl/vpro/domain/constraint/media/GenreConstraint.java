package nl.vpro.domain.constraint.media;

import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractGenreConstraint;
import nl.vpro.domain.media.Genre;
import nl.vpro.domain.media.MediaObject;


/**
 * @author machiel
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genreConstraintType")
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
