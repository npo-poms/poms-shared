package nl.vpro.domain.npo.wonvpp;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.jackson2.Jackson2Mapper;

public class Utils {

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // The jsons can be a bi sloppy
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);

        return mapper;
    }
}
