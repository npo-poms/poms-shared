package nl.vpro.berlijn.domain;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import jakarta.inject.Singleton;

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
    private final ObjectMapper mapper = new ObjectMapper();
    {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.registerModule(new JavaTimeModule());
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        instance = this;
        log.info("Created {}", this);
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
        "XX", languageCode("zxx") // SEE https://publiekeomroep.atlassian.net/browse/VPPM-2238
        ///"", languageCode(null)
    );

    @SneakyThrows
    public ProductMetadata parseProductMetadata(byte[] json) {
        try {
            LanguageCode.setFallbacks(FALLBACKS);
            return mapper.readValue(json, ProductMetadata.class);
        } finally {
            LanguageCode.resetFallBacks();
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
