package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.bind.AbstractJsonIterable;
import nl.vpro.domain.media.UsedLanguage;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class LanguageList {

    private LanguageList() {
    }

    public static class Serializer extends AbstractJsonIterable.Serializer<UsedLanguage> {

        @Override
        protected void serializeValue(UsedLanguage value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeObject(new UsedLanguageWrapper(value));
        }
    }

    public static class Deserializer extends AbstractJsonIterable.Deserializer<UsedLanguage> {

        @Override
        protected UsedLanguage deserializeValue(JsonNode node, DeserializationContext ctxt) throws IOException {
            UsedLanguageWrapper wrapper = ctxt.getParser().getCodec().treeToValue(node, UsedLanguageWrapper.class);
            return wrapper.getUsedLanguage();
        }
    }
}
