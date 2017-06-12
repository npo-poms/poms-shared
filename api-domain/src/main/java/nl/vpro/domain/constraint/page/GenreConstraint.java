package nl.vpro.domain.constraint.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.endsWith;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.page.Page;

/**
 * @author machiel since 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageGenreConstraintType")
public class GenreConstraint extends TextConstraint<Page> {

    public GenreConstraint() {
        caseHandling = CaseHandling.UPPER;
        exact = true;
    }

    public GenreConstraint(String value) {
        if(endsWith(value, "*")) {
            exact = false;
        }
        super.value = removeEnd(removeEnd(value, "*"), ".");
        caseHandling = CaseHandling.UPPER;
    }

    @Override
    public String getESPath() {
        return "genres.id";
    }

    @Override
    public boolean test(Page t) {
        if (exact) {
            return t.getGenres().stream().anyMatch(g -> {
                return StringUtils.equalsIgnoreCase(value, g.getTermId());
            });
        } else {
            return t.getGenres().stream().anyMatch(g -> {
                return StringUtils.startsWith(g.getTermId(), value);
            });
        }
    }
}
