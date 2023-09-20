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

import nl.vpro.domain.api.SimpleMatchType;
import nl.vpro.domain.api.SimpleTextMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
public class SimpleTextMatcherJson extends AbstractTextMatcherJson<SimpleTextMatcher, SimpleMatchType> {
    private static final SimpleTextMatcherJson SERIALIZER = new SimpleTextMatcherJson();

    public SimpleTextMatcherJson() {
        super(SimpleTextMatcher::new, SimpleMatchType::valueOf);
    }

    public static class Serializer extends JsonSerializer<SimpleTextMatcher> {
        @Override
        public void serialize(SimpleTextMatcher value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            SERIALIZER.serialize(value, jgen, provider);
        }
    }

    public static class Deserializer extends JsonDeserializer<SimpleTextMatcher> {
        @Override
        public SimpleTextMatcher deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return SERIALIZER.deserialize(p, ctxt);
        }
    }
}
