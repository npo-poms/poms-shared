/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.api.*;

import static nl.vpro.domain.api.AbstractTextMatcher.DEFAULT_MATCH;
import static nl.vpro.domain.api.TextMatcher.DEFAULT_MATCHTYPE;

/**
 * @author rico
 * @since 4.6
 */
public abstract class AbstractTextMatcherJson<T extends AbstractTextMatcher<S>, S extends MatchType> {

    private static final String VALUE = "value";
    private static final String MATCH = "match";
    private static final String MATCH_TYPE = "matchType";
    private static final String CASE_SENSITIVE= "caseSensitive";
    private static final String FUZZINESS = "fuzziness";
    private static final String SEMANTIC = "semantic";


    protected final Function<String, T> constructor;
    protected final Function<String, S> valueOf;

    protected AbstractTextMatcherJson(Function<String,  T> constructor, Function<String, S> valueOf) {
        this.constructor = constructor;
        this.valueOf = valueOf;
    }


    public final void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if ((value.getMatch() != DEFAULT_MATCH) || !(value.getMatchType().getName().equals(DEFAULT_MATCHTYPE.getName())) || ! value.isCaseSensitive() || value.isSemantic()) {
            jgen.writeStartObject();
            jgen.writeStringField(VALUE, value.getValue());
            if (value.getMatch() != DEFAULT_MATCH) {
                jgen.writeStringField(MATCH, value.getMatch().name());
            }
            if (!value.getMatchType().getName().equals(DEFAULT_MATCHTYPE.getName())) {
                jgen.writeStringField(MATCH_TYPE, value.getMatchType().getName());
            }
            if (! value.isCaseSensitive()) {
                jgen.writeBooleanField(CASE_SENSITIVE, false);
            }
            if (value.getFuzziness() != null) {
                jgen.writeStringField(FUZZINESS, value.getFuzziness());
            }
            if (value.isSemantic()) {
                jgen.writeBooleanField(SEMANTIC, value.isSemantic());
            }
            jgen.writeEndObject();
        } else {
            jgen.writeString(value.getValue());
        }
    }

    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getParsingContext().inObject()) {
            JsonNode jsonNode = jp.readValueAsTree();
            return from(jsonNode);
        } else {
            return constructor.apply(jp.getText());
        }
    }

    public T from(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return constructor.apply(jsonNode.textValue());
        } else {
            JsonNode m = jsonNode.get(MATCH);
            Match match = m == null ? DEFAULT_MATCH : Match.valueOf(m.asText().toUpperCase());
            JsonNode mt = jsonNode.get(MATCH_TYPE);
            S matchType = mt == null ? null : valueOf.apply(mt.asText().toUpperCase());

            T textMatcher = constructor.apply(jsonNode.get(VALUE).asText());
            JsonNode ic = jsonNode.get(CASE_SENSITIVE);
            boolean caseSensitive = ic == null || ic.asBoolean();
            if (! caseSensitive) {
                textMatcher.setCaseSensitive(false);
            }
            JsonNode fuzzinessNode = jsonNode.get(FUZZINESS);
            if (fuzzinessNode != null) {
                String fuzziness = fuzzinessNode.asText().toUpperCase();
                textMatcher.setFuzziness(fuzziness);
            }

            JsonNode semantic = jsonNode.get(SEMANTIC);
            if (semantic != null) {
                textMatcher.setSemantic(semantic.asBoolean());
            }


            textMatcher.setMatch(match);
            textMatcher.setMatchType(matchType);
            return textMatcher;
        }
    }

}
