package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("and")
public class AndPredicateTestResult extends BooleanPredicateTestResult {
    public AndPredicateTestResult(DisplayablePredicate<?> predicate, Object value, boolean b, List<PredicateTestResult> predicates) {
        super(predicate, value, b, predicate.getDefaultBundleKey(), predicates);

    }
    protected AndPredicateTestResult() {

    }
}
