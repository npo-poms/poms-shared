package nl.vpro.domain.media.bind;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.bind.AbstractJsonIterable;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class LanguageList {

    private LanguageList() {
    }

    public static class Serializer extends AbstractJsonIterable.Serializer<Locale> {

        @Override
        protected void serializeValue(Locale value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeObject(new LocaleWrapper(value));
        }
    }

    public static class Deserializer extends AbstractJsonIterable.Deserializer<Locale> {

        @Override
        protected Locale deserializeValue(JsonNode node, DeserializationContext ctxt) throws IOException {
            LocaleWrapper wrapper = ctxt.getParser().getCodec().treeToValue(node, LocaleWrapper.class);
            return wrapper.getLocale();
        }
    }
}
