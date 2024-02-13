/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.el.ELContext;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractAnd<T> implements Constraint<T> {


    protected List<Constraint<T>> constraints = new ArrayList<>();

    protected AbstractAnd() {
    }

    @SafeVarargs
    public AbstractAnd(Constraint<T>... constraints) {
        this.constraints = new ArrayList<>(Arrays.asList(constraints));
    }

    public AbstractAnd(List<Constraint<T>> constraints) {
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
        return constraints == null ||
            constraints.stream()
                .filter(Objects::nonNull)
                .allMatch(p -> p.test(t));
    }

    @Override
    public AndPredicateTestResult testWithReason(@Nullable T t) {
        List<PredicateTestResult> clauses = constraints.stream().map(c -> c.testWithReason(t)).collect(Collectors.toList());
        return DisplayablePredicates.and(this, t, clauses);
    }

    @Override
    public void setELContext(ELContext ctx, Object value, Locale locale, PredicateTestResult result) {
        Constraint.super.setELContext(ctx, value, locale, result);
        AndPredicateTestResult andResult = (AndPredicateTestResult) result;
        List<String> failingClauses = andResult
            .getClauses()
            .stream()
            .filter(p -> ! p.applies())
            .map(p -> p.getDescription(locale))
            .collect(Collectors.toList());
        ResourceBundle bundle = DisplayablePredicates.getBundleForFalse(this, locale);
        String joinedfailingclauses = failingClauses.stream()
            .collect(Collectors.joining(" " + bundle.getString("AND") + " "));
        ctx.getVariableMapper().setVariable("failingclauses",
            FACTORY.createValueExpression(failingClauses, List.class));
        ctx.getVariableMapper().setVariable("joinedfailingclauses",
            FACTORY.createValueExpression(joinedfailingclauses, String.class));
    }
    @Override
    public String toString() {
        return constraints.stream().map(Object::toString).collect(Collectors.joining(" AND "));
    }

    @Override
    public List<String> getDefaultBundleKey() {
        List<String> result = Constraint.super.getDefaultBundleKey();
        result.add(0, "And");
        return result;
    }

}
