package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@JsonTypeName("simple")
public class SimplePredicateTestResult<T> extends PredicateTestResult<T> {
    SimplePredicateTestResult() {

    }

    SimplePredicateTestResult(DisplayablePredicate<T> constraint, T value, boolean applies, List<String> bundleKey) {
        super(constraint, value, applies, bundleKey);
    }

}
