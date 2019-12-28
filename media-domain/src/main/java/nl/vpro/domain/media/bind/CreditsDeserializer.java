package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.media.*;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class CreditsDeserializer extends JsonDeserializer<Credits> {
    @Override
    public Credits deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.readValueAsTree();
        if (node.has("givenName") || node.has("familyName")) {
            return jp.getCodec().treeToValue(node, Person.class);
        } else {
            return jp.getCodec().treeToValue(node, Name.class);

        }

    }
}
