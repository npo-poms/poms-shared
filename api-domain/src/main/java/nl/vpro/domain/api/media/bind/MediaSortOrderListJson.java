package nl.vpro.domain.api.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.*;
import nl.vpro.util.IntegerVersion;
import nl.vpro.util.Version;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class MediaSortOrderListJson {

    private static final IntegerVersion V55 = Version.of(5, 5);

    public static class Serializer extends JsonSerializer<MediaSortOrderList> {

        @Override
        public void serialize(MediaSortOrderList mediaSortOrders, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {

            if (mediaSortOrders != null) {
                boolean backwards = Compatibility.versionBefore(V55) || mediaSortOrders.size() <= 1;
                if(backwards) {
                    for (MediaSortOrder so : mediaSortOrders) {
                        if (so instanceof TitleSortOrder) {
                            backwards = false;
                            break;
                        }
                    }
                }
                if (backwards) {
                    jgen.writeStartObject();
                    for (MediaSortOrder so : mediaSortOrders) {
                        jgen.writeStringField(so.getField().name(), so.getOrder().name());
                    }
                    jgen.writeEndObject();

                } else {
                    jgen.writeStartArray();
                    for (MediaSortOrder so : mediaSortOrders) {
                        if ((so.getOrder() == null || so.getOrder() == Order.ASC) && (!(so instanceof TitleSortOrder))) {
                            jgen.writeString(so.getField().name());
                        } else {
                            jgen.writeObject(so);
                        }
                    }
                    jgen.writeEndArray();
                }
            } else {
                jgen.writeNull();

            }
        }
    }
    public static class Deserializer extends JsonDeserializer<MediaSortOrderList> {

        @Override
        public MediaSortOrderList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            MediaSortOrderList list = new MediaSortOrderList();
            JsonToken token = jsonParser.currentToken();
            switch (token) {
                case START_OBJECT:
                    jsonParser.nextToken();
                    while (jsonParser.hasToken(JsonToken.FIELD_NAME)) {
                        String[] nextField = jsonParser.getText().split(":", 2);
                        MediaSortField field = MediaSortField.valueOf(nextField[0]);
                        jsonParser.nextToken();
                        if (jsonParser.hasToken(JsonToken.VALUE_STRING)) {
                            Order order = Order.valueOf(jsonParser.getText());
                            list.add(new MediaSortOrder(field, order));
                        } else {
                            TitleSortOrder titleSortOrder = jsonParser.readValueAs(TitleSortOrder.class);
                            list.add(titleSortOrder);
                        }
                        jsonParser.nextToken();
                    }
                    break;
                case START_ARRAY:
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        switch(jsonParser.currentToken()) {
                            case START_OBJECT:
                                ObjectNode o = jsonParser.readValueAsTree();
                                if (o.get("field").asText().equals("title")) {
                                    list.add(jsonParser.getCodec().treeToValue(o, TitleSortOrder.class));
                                } else {
                                    list.add(jsonParser.getCodec().treeToValue(o, MediaSortOrder.class));

                                }
                                break;
                            case VALUE_STRING:
                                MediaSortField sf = MediaSortField.valueOf(jsonParser.getText());
                                list.add(new MediaSortOrder(sf));
                                break;
                        }
                    }
                    break;
            }
            return list;
        }
    }
}
