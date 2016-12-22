package nl.vpro.domain.constraint;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
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
