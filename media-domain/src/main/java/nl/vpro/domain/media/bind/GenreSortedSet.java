package nl.vpro.domain.media.bind;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

import nl.vpro.domain.bind.AbstractJsonIterable;
import nl.vpro.domain.media.Genre;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class GenreSortedSet {
    public static class Serializer extends JsonSerializer<SortedSet<Genre>> {

        @Override
        public boolean isEmpty(SerializerProvider provider, SortedSet<Genre> value) {
            return AbstractJsonIterable.defaultIsEmpty(provider, value);

        }

        @Override
        public void serialize(SortedSet<Genre> genres, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeStartArray();
            for (Genre gt : genres) {
                jgen.writeObject(gt);
            }
            jgen.writeEndArray();
        }

    }

    public static class Deserializer extends JsonDeserializer<Iterable<Genre>> {

        @Override
        public Iterable<Genre> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jp.getCodec();
            if (codec == null) {
                // In org/ektorp/impl/QueryResultParser.java#parseRows(JsonParser jp) it does row.doc.traverse()
                // traverse() gives a new JsonParser, but without the original Codec. Seems a bug. But this work around it.
                jp.setCodec(Jackson2Mapper.getInstance());
                codec = jp.getCodec();
            }

            final SortedSet<Genre> types = new TreeSet<>();

            final ArrayNode array = jp.readValueAs(ArrayNode.class);
            for (JsonNode jsonNode : array) {
                Genre type = codec.treeToValue(jsonNode, Genre.class);
                types.add(type);
            }
            return types;
        }
    }
}
