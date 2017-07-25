package nl.vpro.domain.api.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

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
            jgen.writeStartObject();
            if (mediaSortOrders != null) {
                for (MediaSortOrder so : mediaSortOrders) {
                    jgen.writeFieldName(so.getSortField().name());
                    if (so instanceof TitleSortOrder) {
                        TitleSortOrder titleSortOrder = (TitleSortOrder) so;
                        if (titleSortOrder.getTextualType() != null || titleSortOrder.getOwnerType() != null) {
                            jgen.writeObject(titleSortOrder);
                            continue;
                        }
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
            jsonParser.nextToken();
            while (jsonParser.hasToken(JsonToken.FIELD_NAME)) {
                String nextField = jsonParser.getText();
                MediaSortField field =  MediaSortField.valueOf(nextField);
                jsonParser.nextToken();
                if (jsonParser.hasToken(JsonToken.VALUE_STRING)) {
                    Order order = Order.valueOf(jsonParser.getText());
                    list.add(new MediaSortOrder(field, order));
                } else {
                    list.add(jsonParser.readValueAs(TitleSortOrder.class));
                }
                jsonParser.nextToken();
            }
            return list;

        }
    }
}
