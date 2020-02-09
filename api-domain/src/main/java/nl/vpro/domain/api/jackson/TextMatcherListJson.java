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
import nl.vpro.domain.api.TextMatcherList;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TextMatcherListJson extends AbstractTextMatcherListJson<TextMatcherList, TextMatcher, StandardMatchType> {
    private static final TextMatcherJson SERIALIZER = new TextMatcherJson();
    private static final TextMatcherListJson LIST_SERIALIZER = new TextMatcherListJson();

    public TextMatcherListJson() {
        super(TextMatcherList::new, TextMatcher.class, SERIALIZER);
    }


    public static class Serializer extends JsonSerializer<TextMatcherList> {
        @Override
        public void serialize(TextMatcherList value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            LIST_SERIALIZER.serialize(value, gen);
        }
    }
    public static class Deserializer extends JsonDeserializer<TextMatcherList> {
        @Override
        public TextMatcherList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LIST_SERIALIZER.deserialize(p);
        }
    }

}
