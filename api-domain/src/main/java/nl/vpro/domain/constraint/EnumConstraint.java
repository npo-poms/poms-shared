/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.el.ELContext;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.domain.Displayable;

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

    protected EnumConstraint(Class<S> clazz) {
        this.enumClass = clazz;
    }

    protected EnumConstraint(Class<S> clazz, S value) {
        this.enumValue = value;
        this.enumClass = clazz;
    }

    @Override
    @XmlValue
    public final String getValue() {
        return getXmlValue();
    }
    @Override
    public final void setValue(String s) {
        this.enumValue = getByXmlValue(s);
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
        String name = enumValue.name();
        try {
            XmlEnumValue xmlValue = enumClass.getField(name).getAnnotation(XmlEnumValue.class);
            return xmlValue.value();
        } catch (NoSuchFieldException | NullPointerException e) {
            return name;
        }
    }

    private S getByXmlValue(String v) {
        for (S f : enumClass.getEnumConstants()) {

            try {
                XmlEnumValue e = enumClass.getField(f.name()).getAnnotation(XmlEnumValue.class);
                if (e.value().equals(v)) {
                    return f;
                }
            } catch (NoSuchFieldException | NullPointerException ignored) {

            }
        }
        return Enum.valueOf(enumClass, v);
    }

    @Override
    public void setELContext(ELContext ctx, T v, Locale locale, PredicateTestResult<T> result) {
        TextConstraint.super.setELContext(ctx, v, locale, result);
        if (enumValue instanceof Displayable) {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(((Displayable) enumValue).getDisplayName(), String.class));

        } else {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(enumValue.toString(), String.class));
        }
    }


}
