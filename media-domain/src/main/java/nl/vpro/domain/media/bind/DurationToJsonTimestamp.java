package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.media.support.AuthorizedDuration;

/**
 * Adapts the Duration of the media domain. (This actually contains an 'authority', but we don't expose that in json).
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class DurationToJsonTimestamp {

    public static class Serializer extends JsonSerializer<AuthorizedDuration> {

        @Override
        public void serialize(AuthorizedDuration value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value == null || value.get() == null) {
                jgen.writeNull();
            } else {
                jgen.writeNumber(value.get().toMillis());
            }
        }
    }

    /**
     * @since 2.0
     */

    public static class Deserializer extends JsonDeserializer<AuthorizedDuration> {
        @Override
        public AuthorizedDuration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return AuthorizedDuration.ofMillis(jp.getLongValue());
        }
    }
}
