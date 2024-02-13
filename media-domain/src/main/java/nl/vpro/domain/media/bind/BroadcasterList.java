package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.bind.AbstractJsonIterable;
import nl.vpro.domain.user.Broadcaster;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class BroadcasterList {

    private BroadcasterList() {
    }

    public static class Serializer extends AbstractJsonIterable.Serializer<Broadcaster> {

        @Override
        protected void serializeValue(Broadcaster value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value);
        }
    }

    public static class Deserializer extends AbstractJsonIterable.Deserializer<Broadcaster> {

        @Override
        protected Broadcaster deserializeValue(JsonNode node, DeserializationContext ctxt) throws IOException {
            return  ctxt.getParser().getCodec().treeToValue(node, Broadcaster.class);
        }
    }
}
