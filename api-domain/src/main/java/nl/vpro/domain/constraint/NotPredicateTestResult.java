package nl.vpro.domain.constraint;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
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
