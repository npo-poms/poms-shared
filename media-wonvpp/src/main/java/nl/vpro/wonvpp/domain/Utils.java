package nl.vpro.wonvpp.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import nl.vpro.jackson2.Jackson2Mapper;

public class Utils {

    private static final ObjectMapper mapper = Utils.createObjectMapper();


    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // The jsons can be a bi sloppy
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);

        // Ensure empty strings are treated as null for java.util.Locale values
        SimpleModule localeModule = new SimpleModule();
        localeModule.addDeserializer(Locale.class, new StdDeserializer<Locale>(Locale.class) {
            @Override
            public Locale deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonToken t = p.getCurrentToken();
                if (t == JsonToken.VALUE_STRING) {
                    String txt = p.getText().trim();
                    if (txt.length() == 0) {
                        return null;
                    }
                    // try language tag first, fallback to language-only constructor
                    try {
                        return Locale.forLanguageTag(txt);
                    } catch (Exception e) {
                        return new Locale(txt);
                    }
                } else if (t == JsonToken.VALUE_NULL) {
                    return null;
                }
                // Unexpected token - delegate to context to throw an informative exception
                return (Locale) ctxt.handleUnexpectedToken(_valueClass, p);
            }
        });
        mapper.registerModule(localeModule);

        return mapper;
    }

    public static List<CatalogEntry> unmarshal(InputStream stream) throws IOException {
        // Read a top-level JSON array into a List<CatalogEntry>
        List<CatalogEntry> entries = mapper.readerForListOf(CatalogEntry.class)
            .readValue(stream);
        return entries;

    }
}
