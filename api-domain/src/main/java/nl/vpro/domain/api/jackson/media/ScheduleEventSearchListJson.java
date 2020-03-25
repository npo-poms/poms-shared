package nl.vpro.domain.api.jackson.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.api.media.ScheduleEventSearch;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class ScheduleEventSearchListJson {

    private ScheduleEventSearchListJson() {
    }


    public static class Serializer extends JsonSerializer<List<ScheduleEventSearch>> {
        @Override
        public void serialize(List<ScheduleEventSearch> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value.size() == 1) {
                jgen.writeObject(value.iterator().next());
            } else {
                jgen.writeStartArray();
                for (ScheduleEventSearch scheduleEventSearch : value) {
                    jgen.writeObject(scheduleEventSearch);
                }
                jgen.writeEndArray();
            }

        }
    }

    public static class Deserializer extends JsonDeserializer<List<ScheduleEventSearch>> {

        @Override
        public List<ScheduleEventSearch> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if (jp.getParsingContext().inObject()) {
                jp.clearCurrentToken();
                ScheduleEventSearch scheduleEventSearch = jp.readValueAs(ScheduleEventSearch.class);
                return Arrays.asList(scheduleEventSearch);
            } else if (jp.getParsingContext().inArray()) {
                List<ScheduleEventSearch> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<ScheduleEventSearch> i = jp.readValuesAs(ScheduleEventSearch.class);
                while (i.hasNext()) {
                    list.add(i.next());
                }
                return list;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
