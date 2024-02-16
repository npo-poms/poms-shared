package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.api.*;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class DateRangeMatcherListJson {

    private static final String VALUE = "value";
    private static final String MATCH = "match";

    private DateRangeMatcherListJson() {}

    public static class Serializer extends JsonSerializer<DateRangeMatcherList> {
        @Override
        public void serialize(DateRangeMatcherList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value.getMatch() != MatcherList.DEFAULT_MATCH) {
                jgen.writeStartObject();
                jgen.writeArrayFieldStart(VALUE);
                for (DateRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();
                if (value.getMatch() != null) {
                    jgen.writeStringField(MATCH, value.getMatch().name());
                }
                jgen.writeEndObject();
            } else {
                jgen.writeStartArray();
                for (DateRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();

            }
        }
    }

    public static class Deserializer extends JsonDeserializer<DateRangeMatcherList> {

        @Override
        public DateRangeMatcherList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if (jp.getParsingContext().inObject()) {
                JsonNode jsonNode = jp.readValueAsTree();
                JsonNode m = jsonNode.get(MATCH);
                Match match = m == null ? Match.MUST : Match.valueOf(m.asText().toUpperCase());

                List<DateRangeMatcher> list = new ArrayList<>();
                if (jsonNode.has(VALUE)) {
                    for (JsonNode child : jsonNode.get(VALUE)) {
                        DateRangeMatcher dm = jp.getCodec().readValue(child.traverse(), DateRangeMatcher.class);
                        list.add(dm);
                    }
                } else {
                    list.add(jp.getCodec().readValue(jsonNode.traverse(), DateRangeMatcher.class));
                }
                return new DateRangeMatcherList(list, match);
            } else if (jp.getParsingContext().inArray()) {
                List<DateRangeMatcher> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<DateRangeMatcher> i = jp.readValuesAs(DateRangeMatcher.class);
                while (i.hasNext()) {
                    list.add(i.next());
                }
                return new DateRangeMatcherList(list.toArray(new DateRangeMatcher[0]));
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
