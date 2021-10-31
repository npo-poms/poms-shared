package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.bind.AbstractJsonIterable;
import nl.vpro.domain.user.Broadcaster;

/**
 * Used to let the broadcaster list in the MediaObject json be just an array of strings (see MSE-1267)
 * @author Michiel Meeuwissen
 */
public class BroadcasterListToJson extends AbstractJsonIterable.Serializer<Broadcaster> {


    @Override
    protected void serializeValue(Broadcaster value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeString(value.getDisplayName());
    }
}
