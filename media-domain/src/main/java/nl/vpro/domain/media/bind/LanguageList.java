package nl.vpro.domain.media.bind;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class LanguageList {

    private LanguageList() {
    }

    public static class Serializer extends AbstractList.Serializer<Locale> {

        @Override
        protected void serializeValue(Locale value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeObject(new LocaleWrapper(value));
        }
    }

    public static class Deserializer extends AbstractList.Deserializer<Locale> {

        @Override
        protected Locale deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
            LocaleWrapper wrapper = Jackson2Mapper.getInstance().readerFor(LocaleWrapper.class).readValue(node);
            return wrapper.getLocale();
        }
    }
}
