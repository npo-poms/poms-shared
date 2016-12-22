/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import javax.el.ELContext;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Locale;

import nl.vpro.domain.Displayable;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlTransient
public abstract class EnumConstraint<S extends Enum<S>, T> extends TextConstraint<T> {
    
    @XmlTransient
    protected final Class<S> enumClass;

    protected EnumConstraint(Class<S> clazz) {
        this.enumClass = clazz;
    }

    protected EnumConstraint(Class<S> clazz, S value) {
        this.value = getXmlValue(clazz, value);
        this.enumClass = clazz;
    }
 
    private static <S extends Enum<S>> String getXmlValue(Class<S> enumClass, S value) {
        String name = value.name();
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
        super.setELContext(ctx, v, locale, result);
        S enumValue = getByXmlValue(value);
        if (enumValue instanceof Displayable) {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(((Displayable) enumValue).getDisplayName(), String.class));

        } else {
            ctx.getVariableMapper().setVariable("displayablepredicatevalue", FACTORY.createValueExpression(enumValue.toString(), String.class));
        }
    }

   
}
