package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("and")
public class AndPredicateTestResult<T> extends BooleanPredicateTestResult<T> {
    public AndPredicateTestResult(DisplayablePredicate<T> predicate, T value, boolean b, List<PredicateTestResult<T>> predicates) {
        super(predicate, value, b, predicate.getDefaultBundleKey(), predicates);

    }
    protected AndPredicateTestResult() {

    }
}
