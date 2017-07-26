package nl.vpro.domain.api.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.MediaSortField;
import nl.vpro.domain.api.media.MediaSortOrder;
import nl.vpro.domain.api.media.MediaSortOrderList;
import nl.vpro.domain.api.media.TitleSortOrder;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class MediaSortOrderListJson {

    public static class Serializer extends JsonSerializer<MediaSortOrderList> {

        @Override
        public void serialize(MediaSortOrderList mediaSortOrders, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            int titleCount = 0;
            jgen.writeStartObject();
            if (mediaSortOrders != null) {
                for (MediaSortOrder so : mediaSortOrders) {
                    if (so instanceof TitleSortOrder) {
                        jgen.writeFieldName(so.getSortField().name() + ":" + titleCount++);
                        TitleSortOrder titleSortOrder = (TitleSortOrder) so;
                        if (titleSortOrder.getTextualType() != null || titleSortOrder.getOwnerType() != null) {
                            jgen.writeObject(titleSortOrder);
                            continue;
                        }
                    } else {
                        jgen.writeFieldName(so.getSortField().name());
                    }
                    jgen.writeObject(so.getOrder());

                }
            }
            jgen.writeEndObject();
        }
    }
    public static class Deserializer extends JsonDeserializer<MediaSortOrderList> {

        @Override
        public MediaSortOrderList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            MediaSortOrderList list = new MediaSortOrderList();
            JsonToken token = jsonParser.currentToken();
            switch (token) {
                case START_OBJECT:
                    jsonParser.nextToken();
                    while (jsonParser.hasToken(JsonToken.FIELD_NAME)) {
                        String[] nextField = jsonParser.getText().split("\\:", 2);
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
