package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@JsonTypeName("or")
public class OrPredicateTestResult extends BooleanPredicateTestResult {

    OrPredicateTestResult() {

    }
    OrPredicateTestResult(DisplayablePredicate<?> predicate, Object value, boolean applies, List<PredicateTestResult> predicateTestResultList) {
        super(predicate, value, applies, predicate.getDefaultBundleKey(), predicateTestResultList);
    }

    OrPredicateTestResult(DisplayablePredicate<?> predicate, Object value, boolean applies, List<String> bundleKey, List<PredicateTestResult> predicateTestResultList) {
        super(predicate, value, applies, bundleKey, predicateTestResultList);
    }
}
