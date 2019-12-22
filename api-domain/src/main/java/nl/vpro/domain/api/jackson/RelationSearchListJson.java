package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.api.media.RelationSearch;
import nl.vpro.domain.api.media.RelationSearchList;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
public class RelationSearchListJson {


    public static class Serializer extends JsonSerializer<RelationSearchList> {
        @Override
        public void serialize(RelationSearchList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value.size() == 1) {
                jgen.writeObject(value.iterator().next());
            } else {
                jgen.writeStartArray();
                for (RelationSearch relationSearch : value) {
                    jgen.writeObject(relationSearch);
                }
                jgen.writeEndArray();
            }

        }
    }

    public static class Deserializer extends JsonDeserializer<RelationSearchList> {

        @Override
        public RelationSearchList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if (jp.getParsingContext().inObject()) {
                jp.clearCurrentToken();
                RelationSearch rs = jp.readValueAs(RelationSearch.class);
                return new RelationSearchList(rs);
            } else if (jp.getParsingContext().inArray()) {
                List<RelationSearch> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<RelationSearch> i = jp.readValuesAs(RelationSearch.class);
                while (i.hasNext()) {
                    list.add(i.next());
                }
                return new RelationSearchList(list.toArray(new RelationSearch[0]));
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
