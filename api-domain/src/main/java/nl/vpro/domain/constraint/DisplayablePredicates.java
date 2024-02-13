package nl.vpro.domain.constraint;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
class DisplayablePredicates {

    static final String BUNDLE_FALSE = "nl.vpro.domain.api.PredicateTestFalse";
    static final String BUNDLE_TRUE = "nl.vpro.domain.api.PredicateTestTrue";


    private DisplayablePredicates() {
    }

    /**
     * Default implementation of the reason for true. (Empty suffices)
     */
    public static <T> PredicateTestResult testsTrue(DisplayablePredicate<T> predicate, T value, List<String> bundleKey) {
        return new SimplePredicateTestResult(predicate, value, true, bundleKey);
    }

    public static <T> PredicateTestResult testsTrue(DisplayablePredicate<T> predicate, T value) {
        return testsTrue(predicate, value, predicate.getDefaultBundleKey());
    }

    /**
     * Default implementation of the reason for false.
     */
    public static <T> PredicateTestResult testsFalse(DisplayablePredicate<T> predicate, T value, List<String> bundleKey) {
        return new SimplePredicateTestResult(predicate, value, false, bundleKey);
    }

    public static <T> PredicateTestResult testsFalse(DisplayablePredicate<T> predicate, T value) {
        return testsFalse(predicate, value, predicate.getDefaultBundleKey());
    }

    public static <S> AndPredicateTestResult and(AbstractAnd<S> constraint, S value, List<PredicateTestResult> results) {
        boolean applies = true;
        for (PredicateTestResult t : results) {
            applies &= t.applies();
        }
        if (applies) {
            return new AndPredicateTestResult(constraint, value, true, results);
        } else {
            return new AndPredicateTestResult(constraint, value, false, results);
        }
    }

    public static <T> OrPredicateTestResult or(AbstractOr<T> constraint, T value, List<PredicateTestResult>  results) {
        if (results.isEmpty()) {
            return new OrPredicateTestResult(constraint, value, false, Collections.singletonList("orwithoutpredicates"), Collections.emptyList());
        }
        boolean applies = false;
        for (PredicateTestResult t : results) {
            applies |= t.applies();
        }
        if (applies) {
            return new OrPredicateTestResult(constraint, value, true, results);
        } else {
            return new OrPredicateTestResult(constraint, value, false, results);
        }
    }



    public static <T> String getDescriptionTemplate(DisplayablePredicate<T> predicate, Locale locale, List<String> bundleKey, boolean applies) {
        ResourceBundle bundle = applies ? getBundleForTrue(predicate, locale) : getBundleForFalse(predicate, locale);
        for (String bk : bundleKey) {
            if (bundle.containsKey(bk)) {
                return bundle.getString(bk);
            }
        }
        throw new IllegalStateException("No keys " + bundleKey);
    }

    public static ResourceBundle getBundleForFalse(DisplayablePredicate<?> predicate, Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_FALSE, locale);
    }

    public static ResourceBundle getBundleForTrue(DisplayablePredicate<?> predicate, Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_TRUE, locale);
    }

    static Function<ExpressionFactory, ELContext> CONSTRUCTOR = null;
    @SuppressWarnings("unchecked")
    static ELContext createELContext(ExpressionFactory factory) {
        if (CONSTRUCTOR == null) {
            try {
                Class<? extends ELContext> clazz = (Class<? extends ELContext>) Class.forName("jakarta.el.StandardELContext");
                Constructor<? extends ELContext> constructor = clazz.getConstructor(ExpressionFactory.class);
                CONSTRUCTOR = (f) -> {
                    try {
                        return constructor.newInstance(f);
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                        return null;}


                };
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                log.warn(e.getMessage(), e);
                log.warn("Are you running servlet < 3 container?. Falling back to hibernate implementation");
                try {
                    Class<? extends ELContext> clazz = (Class<? extends ELContext>) Class.forName("org.hibernate.validator.internal.engine.messageinterpolation.el.SimpleELContext");
                    Constructor<? extends ELContext> constructor = clazz.getConstructor();
                    CONSTRUCTOR = (f) -> {
                        try {
                            return constructor.newInstance();
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
                            log.error(e2.getMessage(), e2);
                            return null;
                        }

                    };
                } catch (ClassNotFoundException | NoSuchMethodException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
        }
        if (CONSTRUCTOR == null) {
             return null;
        }
        return CONSTRUCTOR.apply(factory);
    }

}
