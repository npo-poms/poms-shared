/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Setter;

import java.util.*;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * @author rico
 * @since 4.6
 */
@Setter
@XmlTransient
public abstract class AbstractTextMatcherList<T extends AbstractTextMatcher<S>, S extends MatchType>
    extends MatcherList<String, T>
    implements Predicate<String> {

    @Valid
    protected List<T> matchers = new ArrayList<>();

    protected AbstractTextMatcherList() {}

    public AbstractTextMatcherList(List<T> matchers) {
        this.matchers = matchers;
    }

    public AbstractTextMatcherList(Match m, List<T> matchers) {
        super(m);
        this.matchers = matchers;
    }

    @Override
    public List<T> asList() {
        return matchers;
    }

    public List<T> getMatchers() {
        return matchers;
    }


    public boolean searchEquals(AbstractTextMatcherList<T, S> b) {
        if (b == null) {
            return false;
        }
        if (size() != b.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            T valuea = get(i);
            T valueb = b.get(i);
            if (valuea.getMatch() == Match.NOT || valueb.getMatch() == Match.NOT) {
                return false;
            }
            if (! searchEquals(valuea, valueb)) {
                return false;
            }

        }
        return true;

    }

    protected boolean searchEquals(T a, T b) {
        if (a == null || b == null) {
            return a == b;
        }
        String aValue = a.getValue();
        String bValue = b.getValue();
        if (aValue == null || bValue == null) {
            return Objects.equals(aValue, bValue);
        }
        if (a.isCaseSensitive() && b.isCaseSensitive()) {
            return aValue.equals(bValue);
        } else {
            return aValue.equalsIgnoreCase(bValue);
        }
    }

    public static <T extends AbstractTextMatcher<S>, S extends MatchType> boolean searchEquals(AbstractTextMatcherList<T, S> a, AbstractTextMatcherList<T, S> b) {
        if (a == null) {
            return b == null;
        }
        return a.searchEquals(b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTextMatcherList<?, ?> that = (AbstractTextMatcherList<?, ?>) o;

        return matchers != null ? matchers.equals(that.matchers) : that.matchers == null;

    }

    @Override
    public int hashCode() {
        return matchers != null ? matchers.hashCode() : 0;
    }
}
