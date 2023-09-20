/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.io.IOException;
import java.util.Objects;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractFilter<T> implements DelegatingDisplayablePredicate<T> {

    protected Constraint<T> constraint;

    public AbstractFilter() {
    }

    public AbstractFilter(Constraint<T> constraint) {
        this.constraint = constraint;
    }

    public Constraint<T> getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint<T> constraint) {
        this.constraint = constraint;
    }
    @Override
    public Constraint<T> getPredicate() {
        return constraint;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AbstractFilter filter)) {
            return false;
        }

        if(!Objects.equals(constraint, filter.constraint)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return constraint != null ? constraint.hashCode() : 0;
    }

    public static class Serializer extends JsonSerializer<Constraint> {
        @Override
        public void serialize(Constraint value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if(value != null) {
                jgen.writeObject(value);
            } else {
                jgen.writeNull();
            }
        }

    }

    public static class DeSerializer extends JsonDeserializer<Constraint> {
        @Override
        public Constraint deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return jp.readValueAs(Constraint.class);
        }
    }
}
