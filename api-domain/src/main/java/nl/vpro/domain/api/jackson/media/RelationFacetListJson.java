package nl.vpro.domain.api.jackson.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.media.RelationFacet;
import nl.vpro.domain.api.media.RelationFacetList;
import nl.vpro.domain.api.media.RelationSearch;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class RelationFacetListJson {

    public static class Serializer extends JsonSerializer<RelationFacetList> {
        @Override
        public void serialize(RelationFacetList facets, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (facets.getFilter() != null || facets.getSubSearch() != null) {
                jgen.writeStartObject();
                if (facets.size() == 1) {
                    jgen.writeObjectField("value", facets.iterator().next());
                } else {
                    jgen.writeArrayFieldStart("value");
                    for (RelationFacet matcher : facets) {
                        jgen.writeObject(matcher);
                    }
                    jgen.writeEndArray();
                }
                if (facets.getFilter() != null) {
                    jgen.writeObjectField("filter", facets.getFilter());
                }
                if (facets.getSubSearch() != null) {
                    jgen.writeObjectField("subSearch", facets.getSubSearch());
                }
                jgen.writeEndObject();
            } else {
                if (facets.size() == 1) {
                    jgen.writeObject(facets.iterator().next());
                } else {
                    jgen.writeStartArray();
                    for (RelationFacet facet : facets.getFacets()) {
                        jgen.writeObject(facet);
                    }
                    jgen.writeEndArray();
                }
            }
        }
    }
    public static class Deserializer extends JsonDeserializer<RelationFacetList> {

        @Override
        public RelationFacetList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if(jp.getParsingContext().inObject()) {
                JsonNode jsonNode = jp.readValueAsTree();
                JsonNode m = jsonNode.get("value");
                if (m == null) {
                    List<RelationFacet> facets = new ArrayList<>();
                    RelationFacet facet = jp.getCodec().readValue(jsonNode.traverse(jp.getCodec()), RelationFacet.class);
                    facets.add(facet);
                    return new RelationFacetList(facets);
                } else {
                    List<RelationFacet> facets = new ArrayList<>();
                    if (m.isArray()) {
                        ArrayNode array = (ArrayNode) m;
                        for (JsonNode facetNode : array) {
                            RelationFacet f = jp.getCodec().readValue(facetNode.traverse(jp.getCodec()), RelationFacet.class);
                            facets.add(f);
                        }
                    } else {
                        JsonNode facetNode = m;
                        RelationFacet f = jp.getCodec().readValue(facetNode.traverse(jp.getCodec()), RelationFacet.class);
                        facets.add(f);
                    }
                    RelationFacetList result = new RelationFacetList(facets);
                    JsonNode filter = jsonNode.get("filter");
                    if (filter != null) {
                        result.setFilter(jp.getCodec().readValue(filter.traverse(jp.getCodec()), MediaSearch.class));
                    }
                    JsonNode subSearch = jsonNode.get("subSearch");
                    if (subSearch != null) {
                        RelationSearch object = jp.getCodec().readValue(subSearch.traverse(jp.getCodec()), RelationSearch.class);
                        result.setSubSearch(object);
                    }

                    return result;

                }
            } else if(jp.getParsingContext().inArray()) {
                List<RelationFacet> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<RelationFacet> i = jp.readValuesAs(RelationFacet.class);
                while(i.hasNext()) {
                    list.add(i.next());
                }
                return new RelationFacetList(list);
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
