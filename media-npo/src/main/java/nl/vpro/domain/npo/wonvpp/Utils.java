package nl.vpro.domain.npo.wonvpp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.jackson2.Jackson2Mapper;

public class Utils {

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // Treat empty JSON objects/values as null for object deserialization
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

       /* // Ensure empty JSON strings are deserialized as null for String properties
        SimpleModule stringEmptyAsNull = new SimpleModule();
        stringEmptyAsNull.addDeserializer(String.class, new StdDeserializer<String>(String.class) {
            private static final long serialVersionUID = 1L;

            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value != null && value.isEmpty()) {
                    return null;
                }
                return value;
            }
        });

        mapper.registerModule(stringEmptyAsNull);
*/
        return mapper;
    }
}
