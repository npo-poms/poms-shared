/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.*;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;;
import javax.el.ELContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;

/**
 * See https://jira.vpro.nl/browse/API-
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractOr<T> implements Constraint<T> {

    protected List<Constraint<T>> constraints = new ArrayList<>();

    protected AbstractOr() {
    }

    @SafeVarargs
    protected AbstractOr(Constraint<T>... constraints) {
        this(new ArrayList<>(Arrays.asList(constraints)));
    }

    protected AbstractOr(List<Constraint<T>> constraints) {
        this.constraints = constraints;
    }

    public List<Constraint<T>> getConstraints() {
        return constraints;
    }

    public void setConstraint(List<Constraint<T>> constraints) {
        this.constraints = constraints;
    }


    @Override
    public boolean test(@Nullable T t) {
        return constraints != null &&
            constraints
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(p -> p.test(t));
    }


    @Override
    public OrPredicateTestResult<T> testWithReason(@Nullable T t) {
        return DisplayablePredicates.or(this, t, testsWithReason(t));

    }

    public List<PredicateTestResult<T>> testsWithReason(@Nullable T t) {
        return constraints.stream().map(c -> c.testWithReason(t)).collect(Collectors.toList());
    }

    @Override
    public void setELContext(ELContext ctx, T value, Locale locale, PredicateTestResult<T> result) {
        Constraint.super.setELContext(ctx, value, locale, result);
        OrPredicateTestResult<T> orResult = (OrPredicateTestResult<T>) result;
        ResourceBundle bundle = DisplayablePredicates.getBundleForFalse(this, locale);
        List<String> clauses = orResult.getClauses()
            .stream().map(p -> p.getDescription(locale))
            .collect(Collectors.toList());
        String joinedClauses = clauses.stream()
            .collect(Collectors.joining(" " + bundle.getString("OR") + " "));
        ctx.getVariableMapper().setVariable("clauses",
            FACTORY.createValueExpression(clauses, List.class));
        ctx.getVariableMapper().setVariable("joinedclauses",
            FACTORY.createValueExpression(joinedClauses, String.class));
    }


    @Override
    public String toString() {
        return constraints.stream().map(Object::toString).collect(Collectors.joining(" OR "));
    }

    @Override
    public List<String> getDefaultBundleKey() {
        List<String> result = Constraint.super.getDefaultBundleKey();
        result.add(0, "Or");
        return result;
    }
}
