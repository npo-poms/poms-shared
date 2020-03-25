package nl.vpro.domain.media.bind;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;


/**
 * @see  <a href="http://jira.codehaus.org/browse/JACKSON-920">jackson</a>
 * @deprecated Jackson2Mapper arranged it with a module in shared-jackson2 >= 0.21
 */
@Deprecated
public class DateToJsonTimestamp {

    private DateToJsonTimestamp() {

    }

    public static class Serializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.getTime());
        }
    }

    /**
    * @since 2.0
     */

    public static class Deserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return new Date(jp.getLongValue());
        }
    }
}
