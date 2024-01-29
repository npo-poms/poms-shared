/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.*;

import jakarta.el.ELContext;
import jakarta.xml.bind.annotation.*;

import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlTransient
public abstract class EnumConstraint<S extends Enum<S>, T> implements TextConstraint<T> {

    @XmlTransient
    protected final Class<S> enumClass;

    @XmlTransient
    protected S enumValue;


    @XmlTransient
    private String value;

    protected EnumConstraint(Class<S> clazz) {
        this.enumClass = clazz;
    }

    protected EnumConstraint(Class<S> clazz, S value) {
        this.enumValue = value;
        this.enumClass = clazz;
    }

    public S getEnumValue() {
        return enumValue;
    }

    @Override
    @XmlValue
    public final String getValue() {
        return getXmlValue();
    }
    public final void setValue(String s) {
        this.enumValue = getByXmlValue(s);
        this.value = null;
    }

    protected abstract Collection<S> getEnumValues(T input);


    protected Collection<S> asCollection(S enumValue) {
        return enumValue == null ? Collections.emptyList() : Collections.singletonList(enumValue);
    }

    @Override
    public boolean test(T input) {
        if (input != null) {
            for (S ev : getEnumValues(input)) {
                if (enumValue == ev) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getXmlValue() {
        if (enumValue == null) {
            return null;
        }
        if (value == null) {
            // caching
            String name = enumValue.name();
            try {
                XmlEnumValue xmlValue = enumClass.getField(name).getAnnotation(XmlEnumValue.class);
                value = xmlValue.value();
            } catch (NoSuchFieldException | NullPointerException e) {
                value = name;
            }
        }
        return value;
    }

    private S getByXmlValue(String v) {
        S inexactMatch = null;
        for (S f : enumClass.getEnumConstants()) {
            try {
                XmlEnumValue e = enumClass.getField(f.name()).getAnnotation(XmlEnumValue.class);
                if (e != null) {
                    if (e.value().equals(v)) {
                        return f;
                    }
                    if (inexactMatch == null && e.value().equalsIgnoreCase(v)) {
                        inexactMatch = f;
                    }
                } else {
                    if (f.name().equals(v)) {
                        return f;
                    }
                    if (inexactMatch == null && f.name().equalsIgnoreCase(v)) {
                        inexactMatch = f;
                    }

                }
            } catch (NoSuchFieldException ignored) {

            }
        }
        if (inexactMatch != null) {
            return inexactMatch;
        }
        throw new IllegalArgumentException("no such enum value " + v);

    }

    @Override
    public void setELContext(ELContext ctx, Object v, Locale locale, PredicateTestResult result) {
        TextConstraint.super.setELContext(ctx, v, locale, result);
        if (enumValue instanceof Displayable) {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(((Displayable) enumValue).getDisplayName(), String.class));

        } else {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(enumValue.toString(), String.class));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getESPath() + "=" + getXmlValue();
    }


}
