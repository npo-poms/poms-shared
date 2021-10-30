package nl.vpro.domain.bind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class AbstractList {

    private AbstractList() {
    }

     public static <T> boolean isEmpty(SerializerProvider provider, Iterable<T> value) {
         JsonInclude.Include incl = provider.getConfig().getDefaultPropertyInclusion().getValueInclusion();
         switch (incl) {
             case NON_NULL:
             case NON_ABSENT:
                 if (value == null) return true;
                 break;
             case NON_EMPTY:
                 if (value == null || ! value.iterator().hasNext()) return true;
                 break;
             default:
         }
         return false;
     }

    public static abstract class Serializer<T> extends JsonSerializer<Iterable<T>> {

        /**
         * TODO: Consider this to be default true.
         *
         * This will fail a lot of test cases that produce empty arrays. But the fields are marked 'NON_EMPTY' !
         */
        boolean considerJsonInclude = false;

        public boolean isEmpty(SerializerProvider provider, Iterable<T> value) {
            if (considerJsonInclude) {
                return AbstractList.isEmpty(provider, value);
            } else {
                return super.isEmpty(provider, value);
            }
        }

        @Override
        public final void serialize(Iterable<T> list, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {


            jgen.writeStartArray();
            for (T value : list) {
                serializeValue(value, jgen, serializerProvider);
            }
            jgen.writeEndArray();
        }

        abstract protected void serializeValue(T value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException;
    }

    public static abstract class Deserializer<T> extends JsonDeserializer<Iterable<T>> {

        @Override
        public Iterable<T> deserialize (JsonParser jp, DeserializationContext ctxt) throws IOException {
            List<T> answer = new ArrayList<>();

            // See https://github.com/helun/Ektorp/pull/184
            // If the pull request is accepted the following code can be removed with ektorp 1.4.3
            if (jp.getCodec() == null) {
                // In org/ektorp/impl/QueryResultParser.java#parseRows(JsonParser jp) it does row.doc.traverse()
                // traverse() gives a new JsonParser, but without the original Codec. Seems a bug. But this work around it.
                jp.setCodec(Jackson2Mapper.INSTANCE);
            }

            ArrayNode array = jp.readValueAs(ArrayNode.class);
            for (JsonNode jsonNode : array) {
                T value = deserialize(jsonNode, ctxt);
                if (value != null) {
                    answer.add(value);
                }
            }
            return answer;
        }

        abstract protected T deserialize(JsonNode node, DeserializationContext ctxt) throws IOException;

    }
}
