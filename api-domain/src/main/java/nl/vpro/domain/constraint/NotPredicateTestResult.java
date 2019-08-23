package nl.vpro.domain.constraint;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("not")
public class NotPredicateTestResult<T> extends PredicateTestResult<T> {

    private PredicateTestResult<T> clause;

    NotPredicateTestResult(DisplayablePredicate<T> predicate, T value, boolean applies, PredicateTestResult<T> predicateTestResult) {
        super(predicate, value, applies, predicate.getDefaultBundleKey());
        this.clause = predicateTestResult;

    }

    @XmlElement
    public PredicateTestResult<T> getClause() {
        return clause;
    }
}
