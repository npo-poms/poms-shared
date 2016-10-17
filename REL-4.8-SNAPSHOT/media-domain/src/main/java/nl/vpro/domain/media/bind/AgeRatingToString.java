package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.media.AgeRating;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class AgeRatingToString {
    public static class Serializer extends JsonSerializer<AgeRating> {

        @Override
        public void serialize(AgeRating value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            String text = value.toString();
            if (text.startsWith("_")) {
                jgen.writeString(text.substring(1));
            } else {
                jgen.writeString(text);
            }

        }
    }

    public static class Deserializer extends JsonDeserializer<AgeRating> {
        @Override
        public AgeRating deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return AgeRating.xmlValueOf(jp.getText());
        }
    }

}
