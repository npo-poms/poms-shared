/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;

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
            final ObjectMapper mapper = Jackson2Mapper.getLenientInstance();

            DateRangeFacets<?> result = new DateRangeFacets<>();

            TreeNode treeNode = jp.getCodec().readTree(jp);
            if(treeNode instanceof ArrayNode arrayNode) {
                for(JsonNode jsonNode : arrayNode) {
                    if(jsonNode.isTextual()) {
                        try {
                            result.addRanges(DateRangePreset.valueOf(jsonNode.textValue()));
                        } catch(IllegalArgumentException e) {
                            result.addRanges(new DateRangeInterval(jsonNode.textValue()));
                        }
                    } else {
                        result.addRanges(mapper.readValue(jsonNode.toString(), DateRangeFacetItem.class));
                    }
                }
            } else if(treeNode instanceof ObjectNode) {
                result.addRanges(mapper.readValue((treeNode).toString(), DateRangeFacetItem.class));
            } else if(treeNode instanceof TextNode) {
                try {
                    result.addRanges(DateRangePreset.valueOf(((TextNode)treeNode).asText()));
                } catch(IllegalArgumentException e) {
                    result.addRanges(new DateRangeInterval(((TextNode)treeNode).textValue()));
                }
            } else {
                throw new IllegalArgumentException("Unsupported node: " + treeNode.toString());
            }

            return result;
        }
    }
}
