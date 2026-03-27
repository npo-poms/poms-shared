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

    private static final ObjectMapper MAPPER = Utils.createObjectMapper();


    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // The JSON can be a bit sloppy, so some leniency features
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);

        SimpleModule localeModule = new SimpleModule();
        localeModule.addDeserializer(Locale.class, new LocaleDeserializer()); // supports accept empty as null
        mapper.registerModule(localeModule);

        return mapper;
    }

    /**
     * Read a top-level JSON array into a List<CatalogEntry>
     * @param stream
     * @return
     * @throws IOException
     */
    public static List<CatalogEntry> unmarshal(InputStream stream) throws IOException {
        //
        return MAPPER.readerForListOf(CatalogEntry.class)
            .readValue(stream);

    }
}
