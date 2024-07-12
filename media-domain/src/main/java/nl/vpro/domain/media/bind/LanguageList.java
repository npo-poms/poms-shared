package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.bind.AbstractJsonIterable;
import nl.vpro.domain.media.UsedLanguage;

/**
 * Might not be needed. It seems that the only thing is currently arranges is that the empy list causes [].
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class LanguageList {

    private LanguageList() {
    }

    public static class Serializer extends AbstractJsonIterable.Serializer<UsedLanguage> {

        @Override
        protected void serializeValue(UsedLanguage value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeObject(new UsedLanguageAdapter.Wrapper(value));
        }
    }

    public static class Deserializer extends AbstractJsonIterable.Deserializer<UsedLanguage> {

        @Override
        protected UsedLanguage deserializeValue(JsonNode node, DeserializationContext ctxt) throws IOException {
            UsedLanguageAdapter.Wrapper wrapper = ctxt.getParser().getCodec().treeToValue(node, UsedLanguageAdapter.Wrapper.class);
            return wrapper.getUsedLanguage();
        }
    }
}
