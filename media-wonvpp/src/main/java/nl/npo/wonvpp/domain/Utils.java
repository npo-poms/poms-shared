package nl.npo.wonvpp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.jackson2.LocaleDeserializer;

public class Utils {

    private static final ObjectMapper mapper = Utils.createObjectMapper();


    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // The jsons can be a bi sloppy
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);

        SimpleModule localeModule = new SimpleModule();
        localeModule.addDeserializer(Locale.class, new LocaleDeserializer());
        mapper.registerModule(localeModule);

        return mapper;
    }

    public static List<CatalogEntry> unmarshal(InputStream stream) throws IOException {
        // Read a top-level JSON array into a List<CatalogEntry>
        return mapper.readerForListOf(CatalogEntry.class)
            .readValue(stream);

    }
}
