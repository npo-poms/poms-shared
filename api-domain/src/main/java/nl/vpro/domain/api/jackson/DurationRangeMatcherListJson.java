package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.DurationRangeMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class DurationRangeMatcherListJson {

    private DurationRangeMatcherListJson() {}

    public static class Serializer extends JsonSerializer<DurationRangeMatcherList> {
        @Override
        public void serialize(DurationRangeMatcherList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value.getMatch() != MatcherList.DEFAULT_MATCH) {
                jgen.writeStartObject();
                jgen.writeArrayFieldStart("value");
                for (DurationRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();
                jgen.writeStringField("match", value.getMatch().name());
                jgen.writeEndObject();
            } else {
                jgen.writeStartArray();
                for (DurationRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();

            }
        }
    }

    public static class Deserializer extends JsonDeserializer<DurationRangeMatcherList> {

        @Override
        public DurationRangeMatcherList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if (jp.getParsingContext().inObject()) {
                JsonNode jsonNode = jp.readValueAsTree();
                JsonNode m = jsonNode.get("match");
                Match match = m == null ? Match.MUST : Match.valueOf(m.asText().toUpperCase());

                List<DurationRangeMatcher> list = new ArrayList<>();
                if (jsonNode.has("value")) {
                    for (JsonNode child : jsonNode.get("value")) {
                        DurationRangeMatcher dm = jp.getCodec().readValue(child.traverse(), DurationRangeMatcher.class);
                        list.add(dm);
                    }
                } else {
                    list.add(jp.getCodec().readValue(jsonNode.traverse(), DurationRangeMatcher.class));
                }
                return new DurationRangeMatcherList(list, match);
            } else if (jp.getParsingContext().inArray()) {
                List<DurationRangeMatcher> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<DurationRangeMatcher> i = jp.readValuesAs(DurationRangeMatcher.class);
                while (i.hasNext()) {
                    list.add(i.next());
                }
                return new DurationRangeMatcherList(list.toArray(new DurationRangeMatcher[0]));
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
