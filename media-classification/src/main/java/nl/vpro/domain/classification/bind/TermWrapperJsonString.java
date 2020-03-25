package nl.vpro.domain.classification.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Used to let the terms list in the MediaObject json be just an array of strings (see MSE-1267)
 *
 * @author Michiel Meeuwissen
 */
public class TermWrapperJsonString {

    private TermWrapperJsonString() {
    }

    public static class Serializer extends JsonSerializer<AbstractTermWrapper> {
        @Override
        public void serialize(AbstractTermWrapper value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.getName());
        }
    }

    public static abstract class Deserializer<T extends AbstractTermWrapper> extends JsonDeserializer<T> {
        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            T wrapper = getTermWrapper();
            wrapper.setName(jp.getText());
            return wrapper;
        }
        protected abstract T getTermWrapper();

    }
}
