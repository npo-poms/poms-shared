package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class BroadcasterList {

    public static class Serializer extends AbstractList.Serializer<Broadcaster> {

        @Override
        protected void serializeValue(Broadcaster value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value);
        }
    }

    public static class Deserializer extends AbstractList.Deserializer<Broadcaster> {

        @Override
        protected Broadcaster deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
            return Jackson2Mapper.getInstance().readerFor(Broadcaster.class).readValue(node);
        }
    }
}
