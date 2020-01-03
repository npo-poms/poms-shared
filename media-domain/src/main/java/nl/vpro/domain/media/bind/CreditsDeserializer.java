package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.media.*;

/**
 * Resolves polymorphism of credits. Make sure to unmarshal as a {@link Person} if that is appropriate, and to {@link Name} otherwise.
 *
 * Using {@link com.fasterxml.jackson.annotation.JsonTypeId} would have been possible too, but that would make the 'objectType' required, and we have to republish everything.
 *
 * There is not real reason, it is simple to recognize by other fields wether we want a Person or not.
 *
 * This class is added as a JsonDeserializer only on {@link Credits}, and to avoid infinite recursion, in all extentions this is overridden by the default again.
 *
 * We do however include {@link Credits#getObjectType()} for the serializer, so other implementors (perhaps we in the future) may choose to switch on that after all.
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class CreditsDeserializer extends JsonDeserializer<Credits> {
    @Override
    public Credits deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.readValueAsTree();
        String objectType = node.has("objectType") ? node.get("objectType").textValue() : null;
        if ((objectType != null && objectType.equals("person")) || (objectType == null && (node.has("givenName") || node.has("familyName")))) {
            return jp.getCodec().treeToValue(node, Person.class);
        } else {
            return jp.getCodec().treeToValue(node, Name.class);

        }

    }
}
