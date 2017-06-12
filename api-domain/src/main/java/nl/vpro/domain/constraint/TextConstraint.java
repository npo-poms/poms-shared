/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import javax.el.ELContext;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.common.collect.Lists;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlTransient
public abstract class TextConstraint<T> implements FieldConstraint<T> {

    public enum CaseHandling {ASIS, LOWER, UPPER, BOTH}

    @XmlTransient
    protected CaseHandling caseHandling = CaseHandling.LOWER;

    @XmlTransient
    protected boolean exact = true;

    @XmlValue
    protected String value;

    protected TextConstraint() {
    }

    protected TextConstraint(String value) {
        this.value = value;
    }

    public CaseHandling getCaseHandling() {
        return caseHandling;
    }

    public boolean isExact() {
        return exact;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "/" + getESPath() + "{value='" + value + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(this == o) {
            return true;
        }

        if(!this.getClass().equals(o.getClass())) {
            return false;
        }

        TextConstraint<?> that = (TextConstraint<?>)o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }


    @Override
    public void setELContext(ELContext ctx, T v, Locale locale, PredicateTestResult<T> result) {
        FieldConstraint.super.setELContext(ctx, v, locale, result);
    }

    @Override
    public List<String> getDefaultBundleKey() {
        return Lists.asList(
            getClass().getSimpleName() + "/" + getESPath() + "/" + value,
            FieldConstraint.super.getDefaultBundleKey().stream().toArray(String[]::new)
        );
    }


    protected boolean applyValue(String compareTo) {
        switch (getCaseHandling()) {
            case ASIS:
                return Objects.equals(value, compareTo);
            case LOWER:
                return Objects.equals(value == null ? null : value.toLowerCase(), compareTo);
            case UPPER:
                return Objects.equals(value == null ? null : value.toUpperCase(), compareTo);
            default:
                return Objects.equals(value == null ? null : value.toUpperCase(), compareTo.toUpperCase());
        }
    }
}
