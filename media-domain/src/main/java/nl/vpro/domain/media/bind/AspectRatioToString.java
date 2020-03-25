package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.media.AspectRatio;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class AspectRatioToString {

    private AspectRatioToString() {
    }

    public static class Serializer extends JsonSerializer<AspectRatio> {

        @Override
        public void serialize(AspectRatio value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.toString());
        }
    }

    public static class Deserializer extends JsonDeserializer<AspectRatio> {
        @Override
        public AspectRatio deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return AspectRatio.fromString(jp.getText());
        }
    }

}
