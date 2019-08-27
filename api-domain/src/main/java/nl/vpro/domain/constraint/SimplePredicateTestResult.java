package nl.vpro.domain.constraint;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@JsonTypeName("simple")
public class SimplePredicateTestResult extends PredicateTestResult {
    SimplePredicateTestResult() {

    }

    SimplePredicateTestResult(DisplayablePredicate<?> constraint, Object value, boolean applies, List<String> bundleKey) {
        super(constraint, value, applies, bundleKey);
    }

}
