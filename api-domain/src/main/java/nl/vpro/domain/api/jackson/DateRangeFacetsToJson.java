/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.time.Instant;
import nl.vpro.domain.api.*;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class DateRangeFacetsToJson {


    private DateRangeFacetsToJson() {}

    public static class Serializer extends JsonSerializer<DateRangeFacets<?>> {

        @Override
        public void serialize(DateRangeFacets<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartArray();
            for(RangeFacet<Instant> item : value.getRanges()) {
                jgen.writeObject(item);
            }
            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<DateRangeFacets<?>> {

        @Override
        public DateRangeFacets<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            
            DateRangeFacets<?> result = new DateRangeFacets<>();

            JsonNode treeNode = jp.readValueAsTree();
            if(treeNode instanceof ArrayNode arrayNode) {
                for(JsonNode jsonNode : arrayNode) {
                    result.addRanges(parse(jp.getCodec(), jsonNode));
                }
            } else {
                result.addRanges(parse(jp.getCodec(), treeNode));
            }
            return result;
        }
    }
    
    private static RangeFacet<Instant> parseAsText(String text) {
        try {
            return DateRangePreset.valueOf(text);
        } catch(IllegalArgumentException e) {
            return new DateRangeInterval(text);
        }
    }
    
    private static RangeFacet<Instant> parseNode(ObjectCodec codec, JsonNode node) throws IOException {
        try (JsonParser parser = node.traverse(codec)) {
            return parser.readValueAs(DateRangeFacetItem.class);   
        }
    }
    
    private static  RangeFacet<Instant> parse(ObjectCodec codec, JsonNode jsonNode) throws IOException {
        if(jsonNode.isTextual()) {
            return parseAsText(jsonNode.textValue());
        } else {
            return parseNode(codec, jsonNode);
        }
    }
    
}
