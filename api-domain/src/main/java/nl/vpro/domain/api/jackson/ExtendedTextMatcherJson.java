/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.api.ExtendedTextMatcher;
import nl.vpro.domain.api.StandardMatchType;

/**
 * @author rico
 * @since 4.6
 */
public class ExtendedTextMatcherJson extends AbstractTextMatcherJson<ExtendedTextMatcher, StandardMatchType> {
    private static final ExtendedTextMatcherJson SERIALIZER = new ExtendedTextMatcherJson();

    public ExtendedTextMatcherJson() {
        super(ExtendedTextMatcher::new, StandardMatchType::valueOf);
    }

    public static class Serializer extends JsonSerializer<ExtendedTextMatcher> {
        @Override
        public void serialize(ExtendedTextMatcher value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            SERIALIZER.serialize(value, jgen, provider);
        }
    }

    public static class Deserializer extends JsonDeserializer<ExtendedTextMatcher> {
        @Override
        public ExtendedTextMatcher deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return SERIALIZER.deserialize(p, ctxt);
        }
    }

}
