package nl.vpro.domain.constraint;

import java.util.stream.Stream;

import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import static org.apache.commons.lang3.Strings.CS;


/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractGenreConstraint<T> extends AbstractTextConstraint<T> {

    {
        caseHandling = CaseHandling.ASIS;
    }
    protected AbstractGenreConstraint() {

    }

    public AbstractGenreConstraint(String value) {
        super(value);
    }

    @Override
    @Pattern(regexp = "3\\.[0-9.]+")
    public void setValue(String s) {
        super.setValue(s);
    }

    @Override
    public String getESPath() {
        return "genres.id";
    }

    @Override
    public boolean isExact() {
        return value == null || !CS.endsWith(value, "*");
    }

    @Override
    public String getWildcardValue() {
        return CS.removeEnd(value, "*");
    }

    @Override
    public boolean test(T  t) {
        if (isExact()) {
            return getTermIds(t).anyMatch(g -> CS.equals(value, g));
        } else {
            return getTermIds(t).anyMatch(g -> CS.startsWith(g, getWildcardValue()));
        }
    }

    protected abstract Stream<String> getTermIds(T t);
}
