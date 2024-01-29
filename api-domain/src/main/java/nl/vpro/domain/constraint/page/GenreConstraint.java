package nl.vpro.domain.constraint.page;

import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractGenreConstraint;
import nl.vpro.domain.page.Genre;
import nl.vpro.domain.page.Page;
/**
 * @author machiel
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageGenreConstraintType")
public class GenreConstraint extends AbstractGenreConstraint<Page> {

    public GenreConstraint() {

    }

    public GenreConstraint(String value) {
        super(value);
    }

    @Override
    protected Stream<String> getTermIds(Page p) {
        return p.getGenres().stream().map(Genre::getTermId);

    }
}
