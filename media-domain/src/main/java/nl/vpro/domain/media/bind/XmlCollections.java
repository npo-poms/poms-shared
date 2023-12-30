package nl.vpro.domain.media.bind;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.media.update.collections.XmlCollection;

public class XmlCollections {

    public static class Serializer extends JsonSerializer<XmlCollection<?>> {
        @Override
        public void serialize(XmlCollection<?> objects, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartArray();
            for (Object o : objects) {
                gen.writeObject(o);
            }
            gen.writeEndArray();
        }
    }


    public static class Deserializer extends JsonDeserializer<XmlCollection<?>> {


        @Override
        public XmlCollection<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            List<?> values = jsonParser.readValueAs(List.class);
            return  new XmlCollection<>(values);
        }
    }
}

