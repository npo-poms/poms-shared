package nl.vpro.berlijn.domain;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Map;

import jakarta.inject.Singleton;

import org.meeuw.i18n.languages.ISO_639;
import org.meeuw.i18n.languages.LanguageCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.berlijn.domain.availability.Availability;
import nl.vpro.berlijn.domain.epg.EPG;
import nl.vpro.berlijn.domain.productmetadata.ProductMetadata;

import static org.meeuw.i18n.languages.LanguageCode.languageCode;

@Log4j2
@Service
@Singleton
public class Parser {

    public static Parser instance;

    public static synchronized Parser getInstance() {
        if (instance == null) {
            new Parser();
        }
        return instance;
    }

    @Getter
    private final ObjectMapper mapper = createMapper();
    {
        instance = this;
        log.info("Created {}", this);
    }

    public  static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        JavaTimeModule timeModule = new JavaTimeModule();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        mapper.setDateFormat(dateFormat);

        mapper.registerModule(timeModule);
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }


    private Parser() {

    }

    @Value("${berlijn.lenient:false}")
    public void setLenient(boolean lenient) {
        if (lenient) {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            log.info("Berlijn json parser in Lenient mode");
        } else {
            mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            log.info("Berlijn json parser in strict mode (failing on unknown properties)");
        }
    }

    // For some reason Whatson simply does not conform to standard.
    private static final Map<String, LanguageCode> FALLBACKS = Map.of(
        "XX", languageCode("zxx"),
        "ZZ", LanguageCode.UNKNOWN
        // SEE https://publiekeomroep.atlassian.net/browse/VPPM-2238
        ///"", languageCode(null)
    );

    @SneakyThrows
    public ProductMetadata parseProductMetadata(byte[] json) {
        try {
            LanguageCode.setFallbacks(FALLBACKS);
            if (!mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                ISO_639.setIgnoreNotFound();
            }
            return mapper.readValue(json, ProductMetadata.class);
        } finally {
            LanguageCode.resetFallBacks();
            ISO_639.removeIgnoreNotFound();

        }
    }

    @SneakyThrows
    public EPG parseEpg(byte[] json) {
        return mapper.readValue(json, EPG.class);
    }

    @SneakyThrows
    public Availability parseAvailability(byte[] json) {
        return mapper.readValue(json, Availability.class);
    }
}
