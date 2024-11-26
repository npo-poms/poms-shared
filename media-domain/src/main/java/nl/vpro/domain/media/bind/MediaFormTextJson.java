package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.domain.media.search.MediaFormText;

public class MediaFormTextJson {

    public static class Serializer extends JsonSerializer<MediaFormText> {

        @Override
        public void serialize(MediaFormText mediaFormText, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (!mediaFormText.needsAttributes()) {
                if (mediaFormText.getValue() != null) {
                    jsonGenerator.writeString(mediaFormText.getValue());
                } else {
                    jsonGenerator.writeNull();
                }
            } else {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("value", mediaFormText.getValue());
                if (mediaFormText.getBooleanOperator() != null) {
                    jsonGenerator.writeStringField("booleanOperator", mediaFormText.getBooleanOperator().name());
                }
                if (mediaFormText.getExactMatching() != null) {
                    jsonGenerator.writeBooleanField("exactMatching", mediaFormText.getExactMatching());
                }
                if (mediaFormText.getImplicitWildcard() != null) {
                    jsonGenerator.writeBooleanField("implicitWildcard", mediaFormText.getImplicitWildcard());
                  }
                jsonGenerator.writeEndObject();
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<MediaFormText> {

        @Override
        public MediaFormText deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken token = jsonParser.currentToken();
            if (token.isScalarValue()) {
                return new MediaFormText(jsonParser.readValueAs(String.class));
            } else {
                ObjectNode treeNode = jsonParser.readValueAsTree();
                String text = treeNode.get("value").asText();
                MediaFormText formText = new MediaFormText(text);
                if (treeNode.has("booleanOperator")) {
                    MediaFormText.BooleanOperator booleanOperator =
                        MediaFormText.BooleanOperator.valueOf(treeNode.get("booleanOperator").asText());
                    formText.setBooleanOperator(booleanOperator);;
                }
                if (treeNode.has("exactMatching")) {
                    if (!treeNode.get("exactMatching").asBoolean()) {
                        formText.setExactMatching(false);
                    }
                }
                if (treeNode.has("implicitWildcard")) {
                    if (!treeNode.get("implicitWildcard").asBoolean()) {
                        formText.setImplicitWildcard(false);
                    }
                }
                return formText;

            }
        }
    }
}
