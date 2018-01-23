package nl.vpro.domain.api.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.api.StandardMatchType;
import nl.vpro.domain.api.TextMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class TextMatcherJson extends AbstractTextMatcherJson<TextMatcher, StandardMatchType> {
    private static final TextMatcherJson SERIALIZER = new TextMatcherJson();

    public TextMatcherJson() {
        super(TextMatcher::new, StandardMatchType::valueOf);
    }

    public static class Serializer extends JsonSerializer<TextMatcher> {
        @Override
        public void serialize(TextMatcher value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            SERIALIZER.serialize(value, jgen, provider);
        }
    }

    public static class Deserializer extends JsonDeserializer<TextMatcher> {
        @Override
        public TextMatcher deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return SERIALIZER.deserialize(p, ctxt);
        }
    }



}
