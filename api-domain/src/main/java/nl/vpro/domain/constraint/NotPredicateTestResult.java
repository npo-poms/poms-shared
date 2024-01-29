package nl.vpro.domain.constraint;

import jakarta.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("not")
public class NotPredicateTestResult extends PredicateTestResult {

    private PredicateTestResult clause;

    NotPredicateTestResult(DisplayablePredicate<?> predicate, Object value, boolean applies, PredicateTestResult predicateTestResult) {
        super(predicate, value, applies, predicate.getDefaultBundleKey());
        this.clause = predicateTestResult;

    }

    @XmlElement
    public PredicateTestResult getClause() {
        return clause;
    }
}
