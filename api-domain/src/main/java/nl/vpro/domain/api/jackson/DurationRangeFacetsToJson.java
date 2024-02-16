/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import nl.vpro.domain.api.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class DurationRangeFacetsToJson {

    private DurationRangeFacetsToJson() {
    }

    public static class Serializer extends JsonSerializer<DurationRangeFacets<?>> {

        @Override
        public void serialize(DurationRangeFacets<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartArray();
            for(RangeFacet<Duration> item : value.getRanges()) {
                jgen.writeObject(item);
            }
            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<DurationRangeFacets<?>> {

        @Override
        public DurationRangeFacets<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            DurationRangeFacets<?> result = new DurationRangeFacets<>();

            TreeNode treeNode = jp.getCodec().readTree(jp);
            if(treeNode instanceof ArrayNode arrayNode) {
                for(JsonNode jsonNode : arrayNode) {
                    if(jsonNode.isTextual()) {
                        result.addRanges(new DurationRangeInterval(jsonNode.textValue()));
                    } else {
                        result.addRanges(jp.getCodec().readValue(jsonNode.traverse(), DurationRangeFacetItem.class));
                    }
                }
            } else if(treeNode instanceof ObjectNode) {
                result.addRanges(jp.getCodec().readValue(treeNode.traverse(), DurationRangeFacetItem.class));
            } else if(treeNode instanceof TextNode) {
                result.addRanges(new DurationRangeInterval(((TextNode)treeNode).textValue()));
            } else {
                throw new IllegalArgumentException("Unsupported node: " + treeNode.toString());
            }

            return result;
        }
    }
}
