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
import nl.vpro.domain.api.ExtendedTextMatcherList;
import nl.vpro.domain.api.StandardMatchType;

/**
 * @author rico
 * @since 4.6
 */
public class ExtendedTextMatcherListJson extends AbstractTextMatcherListJson<ExtendedTextMatcherList, ExtendedTextMatcher, StandardMatchType> {
    private static final ExtendedTextMatcherJson SERIALIZER = new ExtendedTextMatcherJson();
    private static final ExtendedTextMatcherListJson LIST_SERIALIZER = new ExtendedTextMatcherListJson();

    public ExtendedTextMatcherListJson() {
        super(ExtendedTextMatcherList::new, ExtendedTextMatcher.class, SERIALIZER);
    }


    public static class Serializer extends JsonSerializer<ExtendedTextMatcherList> {
        @Override
        public void serialize(ExtendedTextMatcherList value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            LIST_SERIALIZER.serialize(value, gen);
        }
    }
    public static class Deserializer extends JsonDeserializer<ExtendedTextMatcherList> {
        @Override
        public ExtendedTextMatcherList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LIST_SERIALIZER.deserialize(p);
        }
    }


}



