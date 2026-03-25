package nl.vpro.domain.npo.wonvpp;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.domain.user.ServiceLocator;
import nl.vpro.media.broadcaster.BroadcasterServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class CatalogEntryTest {

    ObjectMapper mapper = Utils.createObjectMapper();
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static {
        ServiceLocator.setBroadcasterService(new BroadcasterServiceImpl("https://poms.omroep.nl/broadcasters"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wonvpp.example.json", "wonvpp.example2.json"})

    public void fromJson(String file) throws IOException {

        // Read a top-level JSON array into a List<CatalogEntry>
        List<CatalogEntry> entries = mapper.readerForListOf(CatalogEntry.class)
            .readValue(getClass().getResourceAsStream("/wonvpp/%s".formatted(file)));

        log.info("" + entries);

        // Validate each entry individually (validator.validate expects a bean, not a Collection)
        for (CatalogEntry entry : entries) {
            assertThat(validator.validate(entry)).isEmpty();
        }
    }


}
