package nl.vpro.domain.constraint;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public abstract class BooleanPredicateTestResult<T> extends PredicateTestResult<T> {

    @XmlElementWrapper
    @XmlElement(name = "clause")
    @JsonProperty("clauses")
    private List<PredicateTestResult<T>> clauses;

    BooleanPredicateTestResult() {
        
    }
    
    
    BooleanPredicateTestResult(DisplayablePredicate<T> constraint, T value, boolean applies, List<String> bundleKey, List<PredicateTestResult<T>> predicateTestResults) {
        super(constraint, value, applies, bundleKey);
        this.clauses = predicateTestResults;
    }

  
    public List<PredicateTestResult<T>> getClauses() {
        return clauses;
    }
    
}
