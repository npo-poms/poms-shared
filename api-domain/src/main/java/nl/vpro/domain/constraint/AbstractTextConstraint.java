/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.Locale;
import java.util.Objects;

import javax.el.ELContext;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlTransient
public abstract class AbstractTextConstraint<T> implements WildTextConstraint<T> {


    @XmlTransient
    protected CaseHandling caseHandling = CaseHandling.LOWER;

    @XmlTransient
    protected String value;

    protected AbstractTextConstraint() {
    }

    protected AbstractTextConstraint(String value) {
        this.value = value;
    }



    @Override
    @XmlValue
    public final String getValue() {
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

        AbstractTextConstraint<?> that = (AbstractTextConstraint<?>)o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }


    @Override
    public void setELContext(ELContext ctx, Object v, Locale locale, PredicateTestResult result) {
        WildTextConstraint.super.setELContext(ctx, v, locale, result);
    }


    protected boolean applyValue(String compareTo) {
        return switch (getCaseHandling()) {
            case ASIS -> Objects.equals(value, compareTo);
            case LOWER -> Objects.equals(value == null ? null : value.toLowerCase(), compareTo);
            case UPPER -> Objects.equals(value == null ? null : value.toUpperCase(), compareTo);
            default -> Objects.equals(value == null ? null : value.toUpperCase(), compareTo.toUpperCase());
        };
    }


    @Override
    public CaseHandling getCaseHandling() {
        return caseHandling;
    }
}
