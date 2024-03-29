package nl.vpro.domain.bind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Base classes and utilities for {@link Serializer}s and {@link Deserializer} for {@link Iterable}s
 *
  * @author Michiel Meeuwissen
 * @since 3.0
 */
public class AbstractJsonIterable {

    public final static  ThreadLocal<Boolean> DEFAULT_CONSIDER_JSON_INCLUDE = ThreadLocal.withInitial(() -> false);

    private AbstractJsonIterable() {
    }

    public static <T> boolean isEmpty(SerializerProvider provider, Iterable<T> value) {
        JsonInclude.Include incl = provider.getConfig().getDefaultPropertyInclusion().getValueInclusion();
        switch (incl) {
            case NON_NULL, NON_ABSENT -> {
                if (value == null) return true;
            }
            case NON_EMPTY -> {
                if (value == null || !value.iterator().hasNext()) return true;
            }
            default -> {
            }
        }
        return false;
    }

    public static <T> boolean isEmpty(SerializerProvider provider, Iterable<T> value, boolean considerJsonInclude) {
        if (considerJsonInclude) {
            return AbstractJsonIterable.isEmpty(provider, value);
        } else {
            return (value == null);
        }
    }


    public static <T> boolean defaultIsEmpty(SerializerProvider provider, Iterable<T> value) {
        return isEmpty(provider, value, DEFAULT_CONSIDER_JSON_INCLUDE.get());
    }


    /**
     * A {@link JsonSerializer} specialization that arranges the json array for the {@link Iterable} already.
     * <p>
     * This leaves a simpler {@link #serializeValue(Object, JsonGenerator, SerializerProvider)} abstract method for the implementer to do.
     */
    public static abstract class Serializer<T> extends JsonSerializer<Iterable<T>> {

        /**
         * TODO: Consider this to be default true.
         * <p>
         * This will fail a lot of test cases that produce empty arrays. But the fields are marked 'NON_EMPTY' !
         */
        protected boolean considerJsonInclude = DEFAULT_CONSIDER_JSON_INCLUDE.get();

        @Override
        public boolean isEmpty(SerializerProvider provider, Iterable<T> value) {
            return AbstractJsonIterable.isEmpty(provider, value, considerJsonInclude);
        }

        @Override
        public final void serialize(Iterable<T> list, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            jgen.writeStartArray();
            for (T value : list) {
                serializeValue(value, jgen, serializerProvider);
            }
            jgen.writeEndArray();
        }

        abstract protected void serializeValue(T value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException;
    }

    /**
     * A {@link JsonDeserializer} specialization that arranges parsing of the json array for the {@link Iterable} already.
     * <p>
     * This leaves a simpler {@link #deserializeValue(JsonNode, DeserializationContext)}  abstract method for the implementer to do.
     */
    public static abstract class Deserializer<T> extends JsonDeserializer<Iterable<T>> {

        @Override
        public Iterable<T> deserialize (JsonParser jp, DeserializationContext ctxt) throws IOException {
            final List<T> answer = new ArrayList<>();

            final ArrayNode array = jp.readValueAs(ArrayNode.class);
            for (JsonNode jsonNode : array) {
                T value = deserializeValue(jsonNode, ctxt);
                if (value != null) {
                    answer.add(value);
                }
            }
            return answer;
        }

        abstract protected T deserializeValue(JsonNode node, DeserializationContext ctxt) throws IOException;

    }
}
