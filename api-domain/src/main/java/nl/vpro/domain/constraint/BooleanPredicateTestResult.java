package nl.vpro.domain.constraint;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public abstract class BooleanPredicateTestResult extends PredicateTestResult {

    @XmlElementWrapper
    @XmlElement(name = "clause")
    @JsonProperty("clauses")
    private List<PredicateTestResult> clauses;

    BooleanPredicateTestResult() {

    }


    BooleanPredicateTestResult(DisplayablePredicate<?> constraint, Object value, boolean applies, List<String> bundleKey, List<PredicateTestResult> predicateTestResults) {
        super(constraint, value, applies, bundleKey);
        this.clauses = predicateTestResults;
    }


    public List<PredicateTestResult> getClauses() {
        return clauses;
    }

}
