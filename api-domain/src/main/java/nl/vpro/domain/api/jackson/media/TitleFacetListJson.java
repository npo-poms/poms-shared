package nl.vpro.domain.api.jackson.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.media.TitleFacet;
import nl.vpro.domain.api.media.TitleFacetList;
import nl.vpro.domain.api.media.TitleSearch;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TitleFacetListJson {

    public static class Serializer extends JsonSerializer<TitleFacetList> {
        @Override
        public void serialize(TitleFacetList facets, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (facets.getFacets() == null)  {
                // backwards compatible
                jgen.writeStartObject();
                jgen.writeObjectField("max", facets.getMax());
                jgen.writeObjectField("sort", facets.getSort());
                jgen.writeEndObject();
            } else {
                jgen.writeStartArray();
                if (facets.getMax() != null) {
                    jgen.writeStartObject();
                    jgen.writeObjectField("max", facets.getMax());
                    jgen.writeObjectField("sort", facets.getSort());
                    jgen.writeEndObject();
                }
                for (TitleFacet facet : facets.getFacets()) {
                    jgen.writeObject(facet);
                }
                jgen.writeEndArray();
            }
        }
    }
    public static class Deserializer extends JsonDeserializer<TitleFacetList> {

        @Override
        public TitleFacetList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            TitleFacetList result = new TitleFacetList();
            if(jp.getParsingContext().inObject()) {
                JsonNode jsonNode = jp.readValueAsTree();
                if (! readBackwards(result, jp, jsonNode)) {
                    TitleFacet f = jp.getCodec().readValue(jsonNode.traverse(jp.getCodec()), TitleFacet.class);
                    result.setFacets(Arrays.asList(f));
                }
            } else if(jp.getParsingContext().inArray()) {
                List<TitleFacet> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<JsonNode> i = jp.readValuesAs(JsonNode.class);
                while(i.hasNext()) {
                    JsonNode n = i.next();
                    JsonNode max = n.get("max");
                    if (max != null) {
                        readBackwards(result, jp, n);
                    } else {
                        list.add(jp.getCodec().readValue(n.traverse(jp.getCodec()), TitleFacet.class));
                    }

                }
                if (!list.isEmpty()) {
                    result.setFacets(list);
                }
            } else {
                throw new IllegalStateException();
            }
            return result;
        }
    }

    protected static boolean readBackwards(TitleFacetList result, JsonParser jp, JsonNode jsonNode) throws IOException {
        boolean readbackwards = false;
        JsonNode filter = jsonNode.get("filter");
        if (filter != null) {
            result.setFilter(jp.getCodec().readValue(filter.traverse(jp.getCodec()), MediaSearch.class));
            readbackwards = true;
        }
        JsonNode subSearch = jsonNode.get("subSearch");
        if (subSearch != null) {
            TitleSearch object = jp.getCodec().readValue(subSearch.traverse(jp.getCodec()), TitleSearch.class);
            result.setSubSearch(object);
        }
        JsonNode max = jsonNode.get("max");
        if (max != null) {
            result.setMax(max.intValue());
            readbackwards = true;
        }
        return readbackwards;
    }

}
