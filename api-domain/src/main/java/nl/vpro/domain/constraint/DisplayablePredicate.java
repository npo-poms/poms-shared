package nl.vpro.domain.constraint;

import java.util.*;
import java.util.function.Predicate;

import jakarta.el.ELContext;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;

/**
 * Like a {@link Predicate}, but it also supplies {@link #testWithReason(Object)}, which also informs the caller <em>why</em> the predicate evaluates false.
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public interface DisplayablePredicate<T> extends Predicate<T> {

    /**
     * Returns a reason and result for the predicate evaluation. The default implementation is based on the resource bundle {@link DisplayablePredicates#BUNDLE_TRUE}
     */
    default PredicateTestResult testWithReason(T input) {
        boolean applies = test(input);
        if (applies) {
            return DisplayablePredicates.testsTrue(this, input);
        } else {
            return DisplayablePredicates.testsFalse(this, input);
        }
    }


    default List<String> getDefaultBundleKey() {
        Class<?> c = getClass();
        List<String> result = new ArrayList<>();
        String sn = c.getSimpleName();
        while (StringUtils.isEmpty(sn)) {
            c = c.getSuperclass();
            sn = c.getSimpleName();
        }
        result.add(sn);
        while(c != null && c != DisplayablePredicate.class) {
            Class<?> sc = c.getSuperclass();
            if (sc == null || ! DisplayablePredicate.class.isAssignableFrom(sc)) {
                for (Class<?> i : c.getInterfaces()) {
                    if (DisplayablePredicate.class.isAssignableFrom(i)) {
                        sc = i;
                        break;
                    }
                }
            }
            c = sc;
            if (c != null) {
                result.add(c.getSimpleName());
            }

        }
        return result;
    }
    default String getReason() {
        return getDefaultBundleKey().get(0);
    }

    default void setELContext(ELContext ctx, Object value, Locale locale, PredicateTestResult result) {
        ctx.getVariableMapper().setVariable("value", FACTORY.createValueExpression(value, Object.class));
        if (value instanceof Displayable) {
            ctx.getVariableMapper().setVariable("displayablevalue", FACTORY.createValueExpression(((Displayable) value).getDisplayName(), String.class));

        } else if (value instanceof Enum) {
            ctx.getVariableMapper().setVariable("displayablevalue", FACTORY.createValueExpression(value.toString(), String.class));
        }
        ctx.getVariableMapper().setVariable("predicate", FACTORY.createValueExpression(this, DisplayablePredicate.class));
        ctx.getVariableMapper().setVariable("predicateTestResult", FACTORY.createValueExpression(result, PredicateTestResult.class));
    }


}
