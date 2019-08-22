package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("or")
public class OrPredicateTestResult<T> extends BooleanPredicateTestResult<T> {

    OrPredicateTestResult() {

    }
    OrPredicateTestResult(DisplayablePredicate<T> predicate, T value, boolean applies, List<PredicateTestResult<T>> predicateTestResultList) {
        super(predicate, value, applies, predicate.getDefaultBundleKey(), predicateTestResultList);
    }

    OrPredicateTestResult(DisplayablePredicate<T> predicate, T value, boolean applies, List<String> bundleKey, List<PredicateTestResult<T>> predicateTestResultList) {
        super(predicate, value, applies, bundleKey, predicateTestResultList);
    }
}
