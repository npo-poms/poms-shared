package nl.vpro.domain.constraint;

import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public interface DelegatingDisplayablePredicate<T> extends DisplayablePredicate<T> {

    @Override
    default boolean test(@Nullable T value) {
        DisplayablePredicate<T> predicate = getPredicate();
        return predicate == null || predicate.test(value);
    }

    @Override
    default PredicateTestResult testWithReason(T input) {
        DisplayablePredicate<T> predicate = getPredicate();
        return predicate == null ? DisplayablePredicates.testsTrue(this, input) : predicate.testWithReason(input);
    }

    @Override
    default List<String > getDefaultBundleKey() {
        DisplayablePredicate<T> predicate = getPredicate();
        return predicate == null ? Arrays.asList("default") : getPredicate().getDefaultBundleKey();
    }

    DisplayablePredicate<T> getPredicate();

}
