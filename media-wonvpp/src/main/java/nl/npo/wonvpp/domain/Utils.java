package nl.npo.wonvpp.domain;

import lombok.SneakyThrows;

import java.io.*;
import java.util.List;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.npo.envelope.NotifyEnvelope;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.jackson2.LocaleDeserializer;

public class Utils {

    private static final ObjectMapper MAPPER = Utils.createObjectMapper();


    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = Jackson2Mapper.getStrictInstance();

        // The JSON can be a bit sloppy, so some leniency features
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);


        SimpleModule localeModule = new SimpleModule();
        localeModule.addDeserializer(Locale.class, new LocaleDeserializer()); // supports accept empty as null
        mapper.registerModule(localeModule);

        return mapper;
    }

    /**
     * Read a top-level JSON array into a List<CatalogEntry>
     * @param stream
     * @return
     */
    @SneakyThrows(IOException.class)
    public static List<CatalogEntry> unmarshal(@NonNull InputStream stream) throws JacksonException {
        return MAPPER.readerForListOf(CatalogEntry.class)
            .readValue(stream);
    }

    /**
     * Read a top-level JSON array into a List<CatalogEntry>
     * @param stream
     * @return
     */
    @SneakyThrows(IOException.class)
    public static List<CatalogEntry> unmarshalEnvelop(@NonNull InputStream stream) throws JacksonException {
        NotifyEnvelope envelope = MAPPER.readerFor(NotifyEnvelope.class)
            .readValue(stream);
        return unmarshal(envelope);
    }

    public static List<CatalogEntry> unmarshal(@NonNull NotifyEnvelope envelope) throws JacksonException {
        return unmarshal(new ByteArrayInputStream(envelope.bytes()));
    }
}
