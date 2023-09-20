/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.domain.api.*;

/**
 * @author rico
 * @since 4.6
 */
public abstract class AbstractTextMatcherListJson<L extends AbstractTextMatcherList<T, S>, T extends AbstractTextMatcher<S>,  S extends MatchType> {

    private final BiFunction<List<T>, Match, L> constructor;

    private final Class<T> type;

    private final AbstractTextMatcherJson<T, S> serializer;

    protected AbstractTextMatcherListJson(BiFunction<List<T>, Match, L> constructor, Class<T> type, AbstractTextMatcherJson<T, S> serializer) {
        this.constructor = constructor;
        this.type = type;
        this.serializer = serializer;
    }

    public final void serialize(L value, JsonGenerator jgen) throws IOException {
        if (value.getMatch() != MatcherList.DEFAULT_MATCH) {
            jgen.writeStartObject();
            jgen.writeArrayFieldStart("value");
            for (AbstractTextMatcher<?> matcher : value) {
                jgen.writeObject(matcher);
            }
            jgen.writeEndArray();
            jgen.writeStringField("match", value.getMatch().name());
            jgen.writeEndObject();
        } else {
            if (value.size() == 1) {
                jgen.writeObject(value.get(0));
            } else {
                jgen.writeStartArray();
                for (AbstractTextMatcher<?> matcher : value) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();
            }

        }
    }


    public final L deserialize(JsonParser jp) throws IOException {
        if (jp.getParsingContext().inObject()) {
            JsonNode jsonNode = jp.readValueAsTree();
            if (jsonNode.isTextual()) {
                T matcher = serializer.constructor.apply(jsonNode.textValue());
                List<T> list = new ArrayList<>();
                list.add(matcher);
                return constructor.apply(list, matcher.getMatch());
            } else if (jsonNode.isObject()) {
                JsonNode value = jsonNode.get("value");
                if (value == null) {
                    throw new IllegalArgumentException("No key named 'value' found on " + jsonNode);
                }
                List<T> list = new ArrayList<>();
                Match match = Match.MUST;
                if (value.isTextual()) {
                    list.add(serializer.from(jsonNode));
                } else {
                    JsonNode m = jsonNode.get("match");
                    match = m == null ? Match.MUST : Match.valueOf(m.asText().toUpperCase());
                    for (JsonNode child : value) {
                        list.add(serializer.from(child));
                    }
                }
                return constructor.apply(list, match);
            } else if (jsonNode.isArray()) {
                List<T> list = new ArrayList<>();
                for (JsonNode child : jsonNode) {
                    list.add(serializer.from(child));
                }
                return constructor.apply(list, MatcherList.DEFAULT_MATCH);
            } else {
                throw new IllegalStateException();
            }
        } else if (jp.getParsingContext().inArray()) {
            List<T> list = new ArrayList<>();
            jp.clearCurrentToken();
            Iterator<T> i = jp.readValuesAs(type);
            while (i.hasNext()) {
                list.add(i.next());
            }
            return constructor.apply(list, MatcherList.DEFAULT_MATCH);
        } else {
            throw new IllegalStateException();
        }
    }



}
