package nl.vpro.domain.media.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.media.Net;

/**
 * Serialize Net to overlapping Channel scheme. This is necessary because  Net is an object while Channel is an Enum.
 *
 * @author roekoe
 * @since 2.1
 */
public class NetToString extends JsonSerializer<Net> {

    @Override
    public void serialize(Net value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getId());
    }
}
